package usemp.certh.main;

import usemp.certh.facebookpersonaldatafetcher.FacebookPersonalDataFetcher;
import usemp.certh.inference.likesclassifier.LikesClassifier;
import usemp.certh.inference.preprepilot.Classification;
import usemp.certh.inference.preprepilot.PrePilotClassifier;
import usemp.certh.inference.urlclassifier.URLClassifier;
import usemp.certh.inference.visualconceptsclassifier.VisualConceptsClassifier;
import usemp.certh.scoringvisualization.ScoringVisualization;
import usemp.certh.userDataAccess.UserDataAccess;
import usemp.certh.userDataAccess.UserDataAccessFromFile;

/**
 *
 * @author gpetkos
 * 
 * This is a complete example that shows the different aspects of this package:
 * - Data are first fetched from Facebook
 * - Subsequently they are passed through the four different inference modules
 *   (or three if no visual concepts are available)
 * - The inference modules update the disclosure scores of a user
 * - A web based visualization shows the disclosure scores of the user
 *   (the version of the visualization shown here is somewhat limited in
 *    the sense that the sensitivity cannot change, this would require a 
 *    full working setup with a proper web server).
 * 
 */
public class Main {
 
    public static void main(String[] args){
        String accessToken="EAACEdEose0cBAMxmMEPrGVFdze7DCjCriprD4pVEqrHHotd2iEOUUQz1X1wTIe21KBRrERu2AZCEDtIrru8dsuFDbDCA6vJutuEZCNT2veZCQtKSlWEdmRMut6RZBzZAR0ltxBZCIMZAo5QQFYMbqJZBS7eVsZB7oeOXX1090XZCTBdAZDZD";
        String targetDir="/myFacebookData/";

        /*
        //We first fetch all the user's data from Facebook and store them in 
        //files in the directory specified in the variable targetDir
        FacebookPersonalDataFetcher.fetchData(accessToken, targetDir);
        System.out.println("Fetched data from Facebook and saved to files!");
                
        //This is the object that allows access to the user's data.
        //Please note that UserDataAccess is an interface that defines the 
        //functions that are necessary for accessing the data that the other 
        //modules need. The class UserDataAccessFromFile is a specific 
        //implementation that we provide that works with the files that are 
        //saved from FacebookPersonalDataFetcher. If you need to access user
        //data from a different source, e.g. a mongo database, you will have
        //to put together a new class that implements the UserDataAccess interface.
        UserDataAccess user_data=new UserDataAccessFromFile(targetDir);

        //We then initialize the likes classifier.
        LikesClassifier likesClassifier=new LikesClassifier();
        System.out.println("Loaded likes classifier");

        //Subsequently we initialize the URLs classifier.
        URLClassifier urlClassifier=new URLClassifier();
        System.out.println("Loaded URL classifier");

        //The pre-pilot classifier is initialized.
        PrePilotClassifier prepilotClassifier=new PrePilotClassifier();
        System.out.println("Loaded pre-pilot classifier");
        
        //We now proceed to classify the user's data with each inference module.
        //First with the likes classifier.
        likesClassifier.classify(user_data);
        System.out.println("Executed likes classifier");
        
        //Then with the URLs classifier.
        urlClassifier.classify(user_data);
        System.out.println("Executed URLs classifier");

        //Subsequently with the prepilot classifier.
        Classification classification_data = prepilotClassifier.loadClassificationData(user_data);
        prepilotClassifier.classifyAll(classification_data, user_data);
        System.out.println("Executed pre-pilot classifier");

        //And finally with the visual concepts classifier.
        VisualConceptsClassifier visualConceptsClassifier=new VisualConceptsClassifier();
        visualConceptsClassifier.classify(user_data);
        System.out.println("Executed visual concepts classifier");
        
        //Eventually, after all the inferences have been performed, 
        //the disclosure scores of the user are saved.
        user_data.saveScoringUser();
        System.out.println("Saved scoring user");

         */
        //Finally, the files for a web based visualization of the 
        //disclosure scores are copied into the directory where the files
        //with the facebook data are stored.
        //In order to see it, please open the file [targetDir]/visualization/USEMP.htm 
        ScoringVisualization scoringVisualization=new ScoringVisualization();
        scoringVisualization.copyVisualizationFiles(targetDir);
        System.out.println("Visualization files copied");
        
    }
    
    
}
