/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gpetkos
 */
public class Constants {
    // TODO. First option for linking to mongo: edit the connection details below.
    // The other option is to change the way mongo is accessed in the DisclosureScoringFramework class.
    public static final String mongoHost="localhost";
    public static final String mongoDatabase="disclosure";
    public static final String mongoCollection="usemp_users";
    
    
//    public enum VisibilityLabel {Public, Private, Friends, Custom};
    public enum VisibilityLabel {EVERYONE, FRIENDS_OF_FRIENDS, FRIENDS, CUSTOM, SELF};
    public enum InferenceMechanism {CLASSIFIER_FROM_PRE_PILOT_DATA,VISUAL_CONCEPT_DETECTION}; //This one needs to be updated
//    public enum SupportDataTypes {Likes, Images, Posts};
    
    public static Map<String,Double> defaultSensitivities=new HashMap<String,Double>();
    static{
        defaultSensitivities.put("Demographics", 0.55);
        defaultSensitivities.put("Employment", 0.95);
        defaultSensitivities.put("Relationship", 0.8);
        defaultSensitivities.put("Psychological traits", 0.9);
        defaultSensitivities.put("Sexual profile", 0.65);
        defaultSensitivities.put("Political attitude", 0.85);
        defaultSensitivities.put("Religious beliefs", 0.6);
        defaultSensitivities.put("Health factors", 1.0);
        defaultSensitivities.put("Location", 0.7);
        defaultSensitivities.put("Consumer profile", 0.75);
        defaultSensitivities.put("Demographics/age", 1.0);
        defaultSensitivities.put("Demographics/gender", 1.0);
        defaultSensitivities.put("Demographics/nationality", 1.0);
        defaultSensitivities.put("Demographics/racial origin", 1.0);
        defaultSensitivities.put("Demographics/ethnicity", 1.0);
        defaultSensitivities.put("Demographics/literacy level", 1.0);
    }
    
    
}
