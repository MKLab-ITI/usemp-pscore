package usemp.certh.inference.visualconceptsclassifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import usemp.certh.userDataAccess.UserDataAccess;
import usemp.certh.scoring.Constants;
import usemp.certh.scoring.ScoringUser;

/**
 *
 * @author gpetkos
 * 
 * This class is the implementation of an inference module that examines the 
 * visual concepts detected in the images posted by a user.
 * For instance, when the visual concept "beer" is detected, this can be associated to the 
 * attribute "alcohol" (under the "Health" dimension)
 * 
 */
public class VisualConceptsClassifier {

    private HashMap<String,String> mapping;
    
    private Double defaultConfidence=0.5;


    public VisualConceptsClassifier() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("imagenet_mapping.txt"), "UTF8"));
            mapping=new HashMap<String,String>();

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                if(parts.length<2) System.out.println("LLLLINE:"+line);
                mapping.put(parts[0], parts[1]);
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void classify(UserDataAccess user_data){
        ScoringUser scoringUser = user_data.getScoringUser();

        HashMap<String,Double> conceptsConfidences=user_data.getVisualConceptsMaxConfidence();
        HashMap<String,String> conceptsImages=user_data.getVisualConceptsMaxConfidenceImage();
        
        HashMap<String,HashMap<String,HashSet<String>>> counts=new HashMap<String,HashMap<String,HashSet<String>>>();
        
        for(String concept:conceptsConfidences.keySet()){
            String privacyValue=mapping.get(concept);
            if(privacyValue!=null){
                String[] parts=privacyValue.split("\\$");
                String dimension=parts[0];
                String attribute=parts[1];
                String value=parts[2];
                String dimAtt=dimension+"$"+attribute;
                HashMap<String,HashSet<String>> count=counts.get(dimAtt);
                if(count==null){
                    count=new HashMap<String,HashSet<String>>();
                    counts.put(dimAtt,count);
                }
                HashSet<String> lks=count.get(value);
                if(lks==null){
                    lks=new HashSet<String>();
                    count.put(value,lks);
                }
                lks.add(concept);
            }
        }
 
        
        for(Entry<String,HashMap<String,HashSet<String>>> entry:counts.entrySet()){
            String[] parts=entry.getKey().split("\\$");
            String dimension=parts[0];
            String attribute=parts[1];
            HashMap<String,Double> probs=new HashMap<String,Double>();
            Double totalCount=0.0;
            Boolean uniqueVal=false;
            for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                totalCount=totalCount+ent.getValue().size();
            }
            if(entry.getValue().entrySet().size()==1) uniqueVal=true;           
            
            if(uniqueVal){
                for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                    List<String> pointersToData=new ArrayList<String>();
                    String conc="";
                    for(String concept:ent.getValue()){
                        conc=concept;
                    }
                    String description_en="This value has been associated with "+Constants.confidence_en(conceptsConfidences.get(conc))+" confidence to the following concepts that have been detected in your images:  ";
                    String description_du="Deze resultaten werden geassocieerd met de concepten die we linken aan de beelden die je deelt afgeleid met een "+Constants.confidence_du(conceptsConfidences.get(conc))+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(conceptsConfidences.get(conc))+" konfidens på följande visuella koncept som har upptäckts i dina bilder:";
                   
                    for(String concept:ent.getValue()){
                        conc=concept;
                        pointersToData.add("IMAGE "+conceptsImages.get(concept));
                        description_en=description_en+concept+" , ";
                        description_du=description_du+concept+" , ";
                        description_sw=description_sw+concept+" , ";
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    System.out.println("DETAILS:");
                    System.out.println(dimension);
                    System.out.println(attribute);
                    System.out.println(ent.getKey());
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, conceptsConfidences.get(conc),
                            usemp.certh.scoring.Constants.InferenceMechanism.VISUAL_CONCEPTS_MAPPING,description_en, description_du, description_sw,user_data);
                }
            }
            else{
                for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                    List<String> pointersToData=new ArrayList<String>();
//                    String description="Because the following concepts have been detected in your images: ";
                    String description_en="This value has been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following concepts that have been detected in your images:  ";
                    String description_du="Deze resultaten werden geassocieerd met de concepten die we linken aan de beelden die je deelt afgeleid met een "+Constants.confidence_du(ent.getValue().size()/totalCount)+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(ent.getValue().size()/totalCount)+" konfidens på följande visuella koncept som har upptäckts i dina bilder: ";
                    for(String concept:ent.getValue()){
                        pointersToData.add("IMAGE "+conceptsImages.get(concept));
//                        description=description+concept+" , ";
                        description_en=description_en+concept+" , ";
                        description_du=description_du+concept+" , ";
                        description_sw=description_sw+concept+" , ";
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, ent.getValue().size()/totalCount,
                            usemp.certh.scoring.Constants.InferenceMechanism.VISUAL_CONCEPTS_MAPPING,description_en,description_du, description_sw,user_data);
                }
            }
        }
    }
    
    public HashMap<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(HashMap<String, String> mapping) {
        this.mapping = mapping;
    }

}
