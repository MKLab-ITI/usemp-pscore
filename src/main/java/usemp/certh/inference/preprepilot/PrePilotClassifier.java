package usemp.certh.inference.preprepilot;

import java.io.File;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import cc.mallet.pipe.SerialPipes;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.InstanceList;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import com.restfb.types.User;
import java.io.BufferedReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import usemp.certh.scoring.Constants;
import usemp.certh.scoring.ScoringUser;
import usemp.certh.userDataAccess.UserDataAccess;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 *
 * @author gpetkos
 * @author Eleftherios Spyromitros-Xioufis
 * 
 * This class is the implementation of an inference module that examines the 
 * collection of all the facebook data of a user and predicts different attributes.
 * 
 */
public class PrePilotClassifier {

    private HashMap<String, HashMap<String, Integer>> vocs;
    private HashMap<String, Classifier> models;
    private ParallelTopicModel model20;
    private SerialPipes pipes20;
    private ParallelTopicModel model30;
    private SerialPipes pipes30;
    private ParallelTopicModel model50;
    private SerialPipes pipes50;
    private ParallelTopicModel model100;
    private SerialPipes pipes100;

    private HashMap<String, String> targetToFeatures;
    private HashMap<String, String> attributeToDimension;
    private HashMap<String, String[]> attributeToValues;
    private HashMap<String, String> imageNetMapping;


    public PrePilotClassifier() {
        //TODO: add models without concepts
        String rootWithConcepts="modelsWithConcepts/";
        //String rootWithoutConcepts="modelsWithoutConcepts/";
        
        ClassLoader classLoader = getClass().getClassLoader();
        String vocsFolder=getResourcesPath()+"vocs/";
        String modelsFolder = getResourcesPath() + rootWithConcepts+"models/";
        
        // create a mapping from target attribute to corresponding feature set
        targetToFeatures = new HashMap<String, String>(58);
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream(rootWithConcepts+"bestSetups.csv"), "UTF8"));
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                String tagetName = parts[1];
                String features = parts[3];
                targetToFeatures.put(tagetName, features);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Mapping from attributes to features created!");

        attributeToDimension = new HashMap<String, String>(58);
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("AttributeToDimension.txt"), "UTF8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                String attributeName = parts[0];
                String dimensionName = parts[1];
                attributeToDimension.put(attributeName, dimensionName);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Attribute to dimension mapping created!");

        attributeToValues = new HashMap<String, String[]>(58);
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("AttributesValues.txt"), "UTF8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                String attributeName = parts[0];
                String valuesStr = parts[1];
                String[] values = valuesStr.split("\\$");
                attributeToValues.put(attributeName, values);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Attribute to values sets mapping created!");
        

        // load all feature vocabularies
        vocs = new HashMap<String, HashMap<String, Integer>>(5);
        File dir = new File(vocsFolder);
        String[] filenames = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("-voc.txt");
            }
        });
        for (String filename : filenames) {
            // System.out.println(filename);
            HashMap<String, Integer> voc = readVocabulary(filename);
            // System.out.println(filename);
            String featureType = filename.replace("-voc.txt", "");
            vocs.put(featureType, voc);
        }
        System.out.println("Vocabularies loaded!");

        // load models for all targets
        models = new HashMap<String, Classifier>(58);
        dir = new File(modelsFolder);
        filenames = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".model");
            }
        });
        for (String filename : filenames) {
            // deserialize model
            System.out.println("Loading model: " + filename);
            Classifier model=null;
            try {
                model = (Classifier) weka.core.SerializationHelper.read(classLoader.getResourceAsStream("modelsWithConcepts/models/"+filename));
            } catch (Exception ex) {
                Logger.getLogger(PrePilotClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            String targetName = filename.replace(".model", "");
            models.put(targetName, model);
        }
        System.out.println("Models loaded!");

        //Loading the LDA models
        //First load the 20 topics model and pipeline
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_20.model.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            model20 = (ParallelTopicModel) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_20.pipes.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            pipes20 = (SerialPipes) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
 
        //Then the 30 topics model and pipeline
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_30.model.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            model30 = (ParallelTopicModel) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_30.pipes.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            pipes30 = (SerialPipes) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        

        //Then the 50 topics model and pipeline
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_50.model.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            model50 = (ParallelTopicModel) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_50.pipes.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            pipes50 = (SerialPipes) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        

        //Finally for the 100 topics model and pipeline
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_100.model.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            model100 = (ParallelTopicModel) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        try {
            InputStream outFile = classLoader.getResourceAsStream("ldaModels/LDA_ml_100.pipes.ser");
            ObjectInputStream oos = new ObjectInputStream(outFile);
            pipes100 = (SerialPipes) oos.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
    }

    /**
     * @return the vocs
     */
    public HashMap<String, HashMap<String, Integer>> getVocs() {
        return vocs;
    }

    /**
     * @param vocs the vocs to set
     */
    public void setVocs(HashMap<String, HashMap<String, Integer>> vocs) {
        this.vocs = vocs;
    }

    /**
     * @return the models
     */
    public HashMap<String, Classifier> getModels() {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(HashMap<String, Classifier> models) {
        this.models = models;
    }

    /**
     * @return the model20
     */
    public ParallelTopicModel getModel20() {
        return model20;
    }

    /**
     * @param model20 the model20 to set
     */
    public void setModel20(ParallelTopicModel model20) {
        this.model20 = model20;
    }

    /**
     * @return the pipes20
     */
    public SerialPipes getPipes20() {
        return pipes20;
    }

    /**
     * @param pipes20 the pipes20 to set
     */
    public void setPipes20(SerialPipes pipes20) {
        this.pipes20 = pipes20;
    }

    /**
     * @return the model30
     */
    public ParallelTopicModel getModel30() {
        return model30;
    }

    /**
     * @param model30 the model30 to set
     */
    public void setModel30(ParallelTopicModel model30) {
        this.model30 = model30;
    }

    /**
     * @return the pipes30
     */
    public SerialPipes getPipes30() {
        return pipes30;
    }

    /**
     * @param pipes30 the pipes30 to set
     */
    public void setPipes30(SerialPipes pipes30) {
        this.pipes30 = pipes30;
    }

    /**
     * @return the model50
     */
    public ParallelTopicModel getModel50() {
        return model50;
    }

    /**
     * @param model50 the model50 to set
     */
    public void setModel50(ParallelTopicModel model50) {
        this.model50 = model50;
    }

    /**
     * @return the pipes50
     */
    public SerialPipes getPipes50() {
        return pipes50;
    }

    /**
     * @param pipes50 the pipes50 to set
     */
    public void setPipes50(SerialPipes pipes50) {
        this.pipes50 = pipes50;
    }

    /**
     * @return the model100
     */
    public ParallelTopicModel getModel100() {
        return model100;
    }

    /**
     * @param model100 the model100 to set
     */
    public void setModel100(ParallelTopicModel model100) {
        this.model100 = model100;
    }

    /**
     * @return the pipes100
     */
    public SerialPipes getPipes100() {
        return pipes100;
    }

    /**
     * @param pipes100 the pipes100 to set
     */
    public void setPipes100(SerialPipes pipes100) {
        this.pipes100 = pipes100;
    }

    /**
     * @return the targetToFeatures
     */
    public HashMap<String, String> getTargetToFeatures() {
        return targetToFeatures;
    }

    /**
     * @param targetToFeatures the targetToFeatures to set
     */
    public void setTargetToFeatures(HashMap<String, String> targetToFeatures) {
        this.targetToFeatures = targetToFeatures;
    }

    /**
     * @return the attributeToDimension
     */
    public HashMap<String, String> getAttributeToDimension() {
        return attributeToDimension;
    }

    /**
     * @param attributeToDimension the attributeToDimension to set
     */
    public void setAttributeToDimension(HashMap<String, String> attributeToDimension) {
        this.attributeToDimension = attributeToDimension;
    }

    /**
     * @return the attributeToValues
     */
    public HashMap<String, String[]> getAttributeToValues() {
        return attributeToValues;
    }

    /**
     * @param attributeToValues the attributeToValues to set
     */
    public void setAttributeToValues(HashMap<String, String[]> attributeToValues) {
        this.attributeToValues = attributeToValues;
    }

    /**
     * @return the imageNetMapping
     */
    public HashMap<String, String> getImageNetMapping() {
        return imageNetMapping;
    }

    /**
     * @param imageNetMapping the imageNetMapping to set
     */
    public void setImageNetMapping(HashMap<String, String> imageNetMapping) {
        this.imageNetMapping = imageNetMapping;
    }

    /**
     * This method returns the distribution of confidences for the classes of the given target attribute. If the attribute is numeric, only the
     * predicted value is returned.
     *
     * @param inst
     * @param targetName
     * @return
     * @throws Exception
     */
    public double[] makePrediction(Instance inst, String targetName) throws Exception {
        // check if a valid targetName was supplied
        if (!targetToFeatures.containsKey(targetName)) {
            throw new Exception("Invalid target name!");
        }
        // call the corresponding model to make a prediction
        return models.get(targetName).distributionForInstance(inst);
    }

    /**
     * Creates an instance for the user with the given id for the given target.
     *
     * @param userId
     * @param targetName
     * @return
     * @throws Exception
     */
    public Instance createInstance(Classification user, String targetName) throws Exception {
        String featureType = targetToFeatures.get(targetName);
        
        String[] features = featureType.split("--");
        // Each TreeMap will contain a sparse representation of each feature
        TreeMap<Integer, Double>[] sparseVectors = new TreeMap[features.length];
        int[] totalDims = new int[features.length]; // the dimensionality of each feature
        int[] dims = new int[features.length]; // the number of non-Zero entries of each feature
        for (int i = 0; i < features.length; i++) {
            // create a vector for each feature
            sparseVectors[i] = new TreeMap<Integer, Double>();
            // get the corresponding vocabulary
            HashMap<String, Integer> voc;
            if (features[i].startsWith("concepts")) { // same voc for all concept features
                voc = vocs.get("concepts");
            } else {
                voc = vocs.get(features[i]);
            }

            if (features[i].startsWith("LDA")) {
                switch (features[i]) {
                case "LDA_ml_20":
                    totalDims[i] = 20;
                    break;
                case "LDA_ml_30":
                    totalDims[i] = 30;
                    break;
                case "LDA_ml_50":
                    totalDims[i] = 50;
                    break;
                case "LDA_ml_100":
                    totalDims[i] = 100;
                    break;
                }
            } else {
                totalDims[i] = voc.size(); // the dimensionality of each feature is equal to the vocabulary size
            }
            switch (features[i]) {
            case "likes":
                HashMap<String, Integer> likesIds = user.getLikesIdsCounts(); // get the like ids of the user
                for (Map.Entry<String, Integer> likeId : likesIds.entrySet()) {
                    String id = likeId.getKey();
                    int freq = likeId.getValue();
                    if (freq > 1) { // sanity check
                        throw new Exception("Strange, likes should be unique!");
                    }
                    if (voc.containsKey(id)) { // check if this like has been pruned from the voc
                        int indexInVoc = voc.get(id); // get the index of this like in the voc
                        sparseVectors[i].put(indexInVoc, (double) freq);
                    }
                }
                break;
            case "likesCats":
                HashMap<String, Integer> likesCats = user.getLikesCatsCounts();
                for (Map.Entry<String, Integer> likeCat : likesCats.entrySet()) {
                    String cat = likeCat.getKey();
                    int freq = likeCat.getValue();
                    if (voc.containsKey(cat)) { // check if this cat has been pruned from the voc
                        int indexInVoc = voc.get(cat); // get the index of this cat in the voc
                        sparseVectors[i].put(indexInVoc, (double) freq);
                    }
                }
                break;
            case "likesTerms":
                HashMap<String, Integer> likesTerms = user.getLikesTermsCounts();
                for (Map.Entry<String, Integer> likeTerm : likesTerms.entrySet()) {
                    String term = likeTerm.getKey();
                    int freq = likeTerm.getValue();
                    if (voc.containsKey(term)) { // check if this term has been pruned from the voc
                        int indexInVoc = voc.get(term); // get the index of this like term in the voc
                        sparseVectors[i].put(indexInVoc, (double) freq);
                    }
                }
                break;
            case "messagesTerms":
                Map<String, Integer> terms = user.getMessagesTermsWithCounts();
                for (Map.Entry<String, Integer> termAndFreq : terms.entrySet()) {
                    String term = termAndFreq.getKey();
                    int freq = termAndFreq.getValue();
                    if (voc.containsKey(term)) { // check if this term has been pruned from the voc
                        int indexInVoc = voc.get(term); // get the index of this msg term in the voc
                        sparseVectors[i].put(indexInVoc, (double) freq);
                    }
                }
                break;
            case "concepts-bin":
                Map<String, Integer> concepts = user.getConceptCounts();
                for (Map.Entry<String, Integer> conceptAndFreq : concepts.entrySet()) {
                    String concept = conceptAndFreq.getKey();
                    if (voc.containsKey(concept)) { // some concepts have been pruned from the voc, check that
                        int indexInVoc = voc.get(concept); // get the index of this concept in the voc
                        sparseVectors[i].put(indexInVoc, 1.0);
                    }
                }
                break;
            case "concepts-freq":
                Map<String, Integer> concepts2 = user.getConceptCounts();
                for (Map.Entry<String, Integer> conceptAndFreq : concepts2.entrySet()) {
                    String concept = conceptAndFreq.getKey();
                    int freq = conceptAndFreq.getValue();
                    if (voc.containsKey(concept)) {
                        int indexInVoc = voc.get(concept);
                        sparseVectors[i].put(indexInVoc, (double) freq);
                    }
                }
                break;
            case "concepts-conf":
                Map<String, Double> concepts3 = user.getConceptSums();
                for (Map.Entry<String, Double> conceptAndConf : concepts3.entrySet()) {
                    String concept = conceptAndConf.getKey();
                    double conf = conceptAndConf.getValue();
                    if (voc.containsKey(concept)) {
                        int indexInVoc = voc.get(concept);
                        sparseVectors[i].put(indexInVoc, conf);
                    }
                }
                break;
            case "LDA_ml_20":
                String text20 = user.getUserMLText();
                if (model20 != null && pipes20 != null) {
                    // Create a new instance named "test instance" with empty target
                    // and source fields note we are using the pipes list here
                    InstanceList testing = new InstanceList(pipes20);
                    testing.addThruPipe(new cc.mallet.types.Instance(text20, null, "test instance", null));

                    // here we get an inferencer from our loaded model and use it
                    TopicInferencer inferencer = model20.getInferencer();
                    double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
                    //System.out.println("0\t" + testProbabilities[0]);
                    for (int j = 0; j < 20; j++) {
                        sparseVectors[i].put(j, testProbabilities[j]);
                    }
                } else {
                    System.out.println("LDA model with 20 topics not properly loaded");
                }
                break;
            case "LDA_ml_30":
                String text30 = user.getUserMLText();
                if (model30 != null && pipes30 != null) {
                    // Create a new instance named "test instance" with empty target
                    // and source fields note we are using the pipes list here
                    InstanceList testing = new InstanceList(pipes30);
                    testing.addThruPipe(new cc.mallet.types.Instance(text30, null, "test instance", null));

                    // here we get an inferencer from our loaded model and use it
                    TopicInferencer inferencer = model30.getInferencer();
                    double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
                    //System.out.println("0\t" + testProbabilities[0]);
                    for (int j = 0; j < 30; j++) {
                        sparseVectors[i].put(j, testProbabilities[j]);
                    }
                } else {
                    System.out.println("LDA model with 30 topics not properly loaded");
                }
                break;
            case "LDA_ml_50":
                String text50 = user.getUserMLText();
                if (model50 != null && pipes50 != null) {
                    // Create a new instance named "test instance" with empty target
                    // and source fields note we are using the pipes list here
                    InstanceList testing = new InstanceList(pipes50);
                    testing.addThruPipe(new cc.mallet.types.Instance(text50, null, "test instance", null));

                    // here we get an inferencer from our loaded model and use it
                    TopicInferencer inferencer = model50.getInferencer();
                    double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
                    //System.out.println("0\t" + testProbabilities[0]);
                    for (int j = 0; j < 50; j++) {
                        sparseVectors[i].put(j, testProbabilities[j]);
                    }
                } else {
                    System.out.println("LDA model with 50 topics not properly loaded");
                }
                break;
            case "LDA_ml_100":
                String text100 = user.getUserMLText();
                if (model100 != null && pipes100 != null) {
                    // Create a new instance named "test instance" with empty target
                    // and source fields note we are using the pipes list here
                    InstanceList testing = new InstanceList(pipes100);
                    testing.addThruPipe(new cc.mallet.types.Instance(text100, null, "test instance", null));

                    // here we get an inferencer from our loaded model and use it
                    TopicInferencer inferencer = model100.getInferencer();
                    double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
                    //System.out.println("0\t" + testProbabilities[0]);
                    for (int j = 0; j < 100; j++) {
                        sparseVectors[i].put(j, testProbabilities[j]);
                    }
                } else {
                    System.out.println("LDA model with 100 topics not properly loaded");
                }
                break;
            default:
                break;
            }
            dims[i] = sparseVectors[i].size();

            // System.out.println(userId + " " + sparseVectors[i].toString());
            // apply L2 normalization!
            normalizeL2(sparseVectors[i]);
        }

        // create a sparse instance from sparse vector(s) and apply a global L2 normalization at the same time
        // if there are more than 1 features
        // fast calculation of the global L2 norm
        double globalL2norm = 0;
        for (int i = 0; i < features.length; i++) {
            if (dims[i] > 0) {
                globalL2norm += 1;
            }
        }
        globalL2norm = Math.sqrt(globalL2norm);

        int totalDim = Utils.sum(totalDims) + 2; // +1 for the userid +1 for the target
        int totalNonZero = Utils.sum(dims) + 2;

        double[] attValues = new double[totalNonZero];
        int[] indices = new int[totalNonZero];

        int curStartIndex = 1; // skip the userId
        int nonZeroIndex = 1; // skip the userId
        for (int i = 0; i < features.length; i++) {
            for (Map.Entry<Integer, Double> e : sparseVectors[i].entrySet()) {
                indices[nonZeroIndex] = curStartIndex + e.getKey();
                double val = e.getValue();
                if (features.length > 1) { // apply global L2 if more than 1 features
                    val /= globalL2norm; // normalize the value
                }
                attValues[nonZeroIndex] = val;
                nonZeroIndex++;
            }
            curStartIndex += totalDims[i];
        }

        Instance inst = new SparseInstance(1.0, attValues, indices, totalDim);

        return inst;
    }

    /**
     * Applies L2 normalization on the given sparse vector.
     *
     * @param sparseVector
     */
    public static void normalizeL2(TreeMap<Integer, Double> sparseVector) {
        // compute L2 norm first
        double l2Norm = 0;
        for (Map.Entry<Integer, Double> e : sparseVector.entrySet()) {
            double val = e.getValue();
            l2Norm += val * val;
        }
        l2Norm = Math.sqrt(l2Norm);
        // now normalize
        for (Map.Entry<Integer, Double> e : sparseVector.entrySet()) {
            int index = e.getKey();
            double val = e.getValue();
            sparseVector.put(index, val / l2Norm);
        }

    }

    public static String numericToString(Double num) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        return "_" + df.format(num);
    }

    public String[] getClassNames(String targetName) {
        return attributeToValues.get(targetName);
    }

    /**
     * This method creates and returns a Classification object that contains all user-related data that are required for performing the
     * classifications.
     * 
     * @param userID
     * @return
     * @throws Exception
     */
//    public Classification loadClassificationData(String userID) throws Exception {
    public Classification loadClassificationData(UserDataAccess user_data) {
        // We first load the data for the user that with the id specified by the single argument of this function
        User user=user_data.getUser();
        Classification classificationData = new Classification(user.getId());

        HashSet<Post> posts=user_data.getAllPosts();
        int msgCounter = 0;
        for (Post post:posts) {
            if (post != null) {
//                logger.debug("#" + msgCounter + ": " + message);
                // if (!message.isEmpty()) {
                classificationData.addPost(post);
                // }
            } 
            //else {
                //System.out.println("#" + msgCounter + ": NULL");
            //}
            msgCounter++;
        }

        HashSet<StatusMessage> statuses=user_data.getAllStatuses();
        //int msgCounter = 0;
        for (StatusMessage status:statuses) {
            if (status != null) {
//                logger.debug("#" + msgCounter + ": " + message);
                // if (!message.isEmpty()) {
                classificationData.addStatus(status);
                // }
            } 
            //else {
                //System.out.println("#" + msgCounter + ": NULL");
            //}
            msgCounter++;
        }
        
        
//        List<Category> userlikes = mongoOperation.findById(userID, UserLikes.class).getLikes();
        HashSet<Page> userLikes=user_data.getAllLikes();

        int likeCounter = 0;
        for (Page like : userLikes) {
            //logger.debug("#" + likeCounter + " id: " + likeId + " name: " + l.getName());
            if (like != null) {
                try {
                    classificationData.addLike(like);
                } catch (Exception ex) {
                    Logger.getLogger(PrePilotClassifier.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
            likeCounter++;
        }

        HashMap<String,Integer> conceptsFrequency=user_data.getVisualConceptsFrequency();
        HashMap<String,Double> conceptsTotalConfidence=user_data.getVisualConceptsTotalConfidence();
        
        for (String concept : conceptsFrequency.keySet()) {
            classificationData.addConcept(concept, conceptsFrequency.get(concept), conceptsTotalConfidence.get(concept));
        }
        return classificationData;
    }

    public void classifyAll(Classification classificationData, UserDataAccess user_data){
        double defaultNumericConfidence = 0.5;
        // Once we have that, we loop over the (58 at the moment) attributes
        // that this inference module can
        // predict
        ScoringUser scoringUser = user_data.getScoringUser();
        
        for (String attribute : attributeToDimension.keySet()) {
            String[] classNames = this.getClassNames(attribute);
            // For each of the 58 attributes to be predicted:
            // 1) We first get the input to the classifier, in the appropriate
            // form for the current
            // user-attribute pair
            
            Instance inst=null;
            try {
                inst = createInstance(classificationData, attribute);
            } catch (Exception ex) {
                Logger.getLogger(PrePilotClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            // System.out.println(inst.toStringNoWeight());
            // 2) We then get the actual prediction
            double[] pred=null;
            try {
                pred = makePrediction(inst, attribute);
            } catch (Exception ex) {
                Logger.getLogger(PrePilotClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }

            // That's it, we have the predictions! We can then just print them
            // out using for instance the
            // following 4 lines (uncomment if you want to check the results)
            // System.out.print("Attribute: " + attribute + " / ");
            // for (int i = 0; i < pred.length; i++)
            // System.out.print(classNames[i] + ":" + pred[i] + "\t");
            // System.out.println();
            // Once we have the prediction results, we can then feed them into the scoring framework using for instance the commented code up to
            // the end of the block
            // But let's leave that for after we are done with the integration of the scoring framework, for the moment let's make sure that the
            // inference module works properly
            String[] attributeValues = attributeToValues.get(attribute);
            String attributeDimension = attributeToDimension.get(attribute);
            // System.out.println(attributeDimension + " " + attribute);
            if (attributeValues.length == 1) {
//                scoringUser.addValue(attributeDimension, attribute, true, numericToString(pred[0]), null);
                if(attribute.equals("agreeableness")){
                    scoringUser.addValue(attributeDimension, attribute, true, "agreeable", null);
                    scoringUser.addValue(attributeDimension, attribute, true, "disagreeable", null);
                }
                if(attribute.equals("conscientiousness")){
                    scoringUser.addValue(attributeDimension, attribute, true, "conscientious", null);
                    scoringUser.addValue(attributeDimension, attribute, true, "unconscientious", null);
                }
                if(attribute.equals("extraversion")){
                    scoringUser.addValue(attributeDimension, attribute, true, "extravert", null);
                    scoringUser.addValue(attributeDimension, attribute, true, "introvert", null);
                }
                if(attribute.equals("neuroticism")){
                    scoringUser.addValue(attributeDimension, attribute, true, "neurotic", null);
                    scoringUser.addValue(attributeDimension, attribute, true, "stable", null);
                }
                if(attribute.equals("openness")){
                    scoringUser.addValue(attributeDimension, attribute, true, "open", null);
                    scoringUser.addValue(attributeDimension, attribute, true, "closed", null);
                }
            } else {
                for (int i = 0; i < pred.length; i++) {
                    scoringUser.addValue(attributeDimension, attribute, true, attributeValues[i], null);
                }
            }
            List<String> pointersToData = new ArrayList<String>();
            String featureType = targetToFeatures.get(attribute);
            String[] features = featureType.split("--");
//            String description="This inference result is based on all ";
            

            String description_feats_en="all ";
            String description_feats_du="";
            String description_feats_sw="";
            int count_feats=0;
            boolean likes=false;
            boolean posts=false;
            boolean concepts=false;
            for (int i = 0; i < features.length; i++) {
                if (features[i].startsWith("LDA_ml")) {
                    pointersToData.add("POST *");
                    pointersToData.add("LIKE *");
                    likes=true;
                    posts=true;
                }
                if (features[i].startsWith("likes")) {
                    pointersToData.add("LIKE *");
                    likes=true;
                }
                if (features[i].startsWith("messages")) {
                    pointersToData.add("POST *");
                    posts=true;
                }
                if (features[i].startsWith("concepts")) {
                    pointersToData.add("IMAGE *");
                    concepts=true;
                }
            }
            if(likes) count_feats=count_feats+1;
            if(posts) count_feats=count_feats+1;
            if(concepts) count_feats=count_feats+1;
            int count_shown=0;
            if(count_feats==3){
                description_feats_en=description_feats_en+" your likes, your posts and the visual concepts detected in your images";// (please check your <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\"> profile!)</a>";
                description_feats_du=description_feats_du+" vind-ik-leuks, posts, afbeeldingen";// (bekijk je <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\">profiel</a> a.u.b.!)";
                description_feats_sw=description_feats_sw+" gilla, inlägg, bilder"; // (vänligen kontrollera din <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\">profil</a>!)";
                
            }
            if(count_feats==1){
                if(likes){
                    count_shown=count_shown+1;
                    description_feats_en=description_feats_en+" your likes";
                    description_feats_du=description_feats_du+" vind-ik-leuks";
                    description_feats_sw=description_feats_sw+" gilla";
                }
                if(posts){
                    description_feats_en=description_feats_en+" your posts";
                    description_feats_du=description_feats_du+" posts";
                    description_feats_sw=description_feats_sw+" inlägg";
                }
                if(concepts){
                    description_feats_en=description_feats_en+" the visual concepts detected in your images";
                    description_feats_du=description_feats_du+" afbeeldingen";
                    description_feats_sw=description_feats_sw+" bilder";
                }
                
                
            }
            if(count_feats==2){
                if(likes){
                    count_shown=count_shown+1;
                    description_feats_en=description_feats_en+" your likes and";
                    description_feats_du=description_feats_du+" vind-ik-leuks, ";
                    description_feats_sw=description_feats_sw+" gilla,";
                }
                if(posts){
                    if(count_shown==1){
                        description_feats_en=description_feats_en+" your posts";
                        description_feats_du=description_feats_du+" posts";
                        description_feats_sw=description_feats_sw+" inlägg";
                    }
                    else{
                        description_feats_en=description_feats_en+" your posts and";
                        description_feats_du=description_feats_du+" posts, ";
                        description_feats_sw=description_feats_sw+" inlägg, ";
                        
                    }
                }
                if(concepts){
                    description_feats_en=description_feats_en+" the visual concepts detected in your images";
                    description_feats_du=description_feats_du+" afbeeldingen";
                    description_feats_sw=description_feats_sw+" bilder";
                }
                
            }

            description_feats_en=description_feats_en+" (please check your <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\"> profile</a>!)";
            description_feats_du=description_feats_du+" (bekijk je <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\">profiel</a> a.u.b.!)";
            description_feats_sw=description_feats_sw+" (vänligen kontrollera din <a target=\"_blank\" href=\"http://www.facebook.com\\"+scoringUser.getUser_id()+"\">profil</a>!)";
            
            
            if (attributeValues.length == 1) {
                String description_en="These results have been inferred with "+Constants.confidence_en(defaultNumericConfidence)+" confidence based on "+description_feats_en;
                String description_du="Deze resultaten werden van je "+description_feats_du+" afgeleid met een "+Constants.confidence_en(defaultNumericConfidence)+" zekerheid";
                String description_sw="Dessa resultat har beräknats med "+Constants.confidence_sw(defaultNumericConfidence)+" konfidens baserat på dina "+description_feats_sw;
  

                if(attribute.equals("agreeableness")){
                    double val=pred[0];
                    if(val>5) val=5;
                    if(val<1) val=1;
                    double agreeable_conf=(val-1)/4;
                    double disagreeable_conf=1-agreeable_conf;
                    scoringUser.addSupport(attributeDimension, attribute, "agreeable", pointersToData, agreeable_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                    scoringUser.addSupport(attributeDimension, attribute, "disagreeable", pointersToData, disagreeable_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                }
                if(attribute.equals("conscientiousness")){
                    double val=pred[0];
                    if(val>5) val=5;
                    if(val<1) val=1;
                    double conscientious_conf=(val-1)/4;
                    double unconscientious_conf=1-conscientious_conf;
                    scoringUser.addSupport(attributeDimension, attribute, "conscientious", pointersToData, conscientious_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                    scoringUser.addSupport(attributeDimension, attribute, "unconscientious", pointersToData, unconscientious_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                }
                if(attribute.equals("extraversion")){
                    double val=pred[0];
                    if(val>5) val=5;
                    if(val<1) val=1;
                    double extravert_conf=(val-1)/4;
                    double introvert_conf=1-extravert_conf;
                    scoringUser.addSupport(attributeDimension, attribute, "extravert", pointersToData, extravert_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                    scoringUser.addSupport(attributeDimension, attribute, "introvert", pointersToData, introvert_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                }
                if(attribute.equals("neuroticism")){
                    double val=pred[0];
                    if(val>5) val=5;
                    if(val<1) val=1;
                    double neurotic_conf=(val-1)/4;
                    double stable_conf=1-neurotic_conf;
                    scoringUser.addSupport(attributeDimension, attribute, "neurotic", pointersToData, neurotic_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                    scoringUser.addSupport(attributeDimension, attribute, "stable", pointersToData, stable_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                }
                if(attribute.equals("openness")){
                    double val=pred[0];
                    if(val>5) val=5;
                    if(val<1) val=1;
                    double open_conf=(val-1)/4;
                    double closed_conf=1-open_conf;
                    scoringUser.addSupport(attributeDimension, attribute, "open", pointersToData, open_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                    scoringUser.addSupport(attributeDimension, attribute, "closed", pointersToData, closed_conf,
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw, user_data);
                }
            } else {
                for (int i = 0; i < pred.length; i++) {
                    
                    
                    String description_en="These results have been inferred with "+Constants.confidence_en(pred[i])+" confidence based on "+description_feats_en;
                    String description_du="Deze resultaten werden van je "+description_feats_du+" afgeleid met een "+Constants.confidence_en(pred[i])+" zekerheid";
                    String description_sw="Dessa resultat har beräknats med "+Constants.confidence_sw(pred[i])+" konfidens baserat på dina "+description_feats_sw;
                    scoringUser.addSupport(attributeDimension, attribute, attributeValues[i], pointersToData, pred[i],
                            usemp.certh.scoring.Constants.InferenceMechanism.CLASSIFIER_FROM_PRE_PILOT_DATA,description_en,description_du, description_sw,user_data);
                }
            }
        }
    }

    private String getResourcesPath() {
        URL url = this.getClass().getClassLoader().getResource("");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            String result=file.getAbsolutePath();
            if((!result.endsWith("/"))&&(!result.endsWith("\\")))
                result=result+"/";
            return result;
        }
    }    
    
    private HashMap<String, Integer> readVocabulary(String vocFilePath) {
        HashMap<String, Integer> voc = new HashMap<String, Integer>();
        try{
            ClassLoader classLoader = getClass().getClassLoader();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("vocs/"+vocFilePath), "UTF8"));

            br.readLine(); // skip header line
            String line;
            while ((line = br.readLine()) != null) {
                String parts[] = line.split(",");
                int index = Integer.parseInt(parts[0]);
                String term = parts[1];
                voc.put(term, index);
            }
            br.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return voc;
    }
    
}
