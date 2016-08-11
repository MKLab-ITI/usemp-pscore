package usemp.certh.scoring;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gpetkos
 */
public class Constants {
    
    
//    public enum VisibilityLabel {Public, Private, Friends, Custom};
    public enum VisibilityLabel {EVERYONE, FRIENDS_OF_FRIENDS, FRIENDS, CUSTOM, SELF};
    public enum InferenceMechanism {CLASSIFIER_FROM_PRE_PILOT_DATA,VISUAL_CONCEPTS_MAPPING,URLS_MAPPING,LIKES_MAPPING}; 
//    public enum SupportDataTypes {Likes, Images, Posts};
    
    public static Map<String,Double> defaultSensitivities=new HashMap<String,Double>();
    static{
        defaultSensitivities.put("Demographics", 0.46904);
        defaultSensitivities.put("Employment", 0.83132);
        defaultSensitivities.put("Relationships", 0.59166);
        defaultSensitivities.put("Psychology", 0.77142);
        defaultSensitivities.put("Sexuality", 0.54438);
        defaultSensitivities.put("Politics", 0.71758);
        defaultSensitivities.put("Religion", 0.53128);
        defaultSensitivities.put("Health", 0.9381);
        defaultSensitivities.put("Location", 0.58682);
        defaultSensitivities.put("Hobbies", 0.61342);
    }
    
    public static String confidence_en(Double confidence){
        if(confidence<0.25) return "very low";
        if(confidence<0.5) return "low";
        if(confidence<0.75) return "medium";        
        return "high";
    }

    public static String confidence_du(Double confidence){
        if(confidence<0.25) return "zeer lage";
        if(confidence<0.5) return "lage";
        if(confidence<0.75) return "gemiddelde";        
        return "hoge";
    }
    
    public static String confidence_sw(Double confidence){
        if(confidence<0.25) return "mycket låg";
        if(confidence<0.5) return "låg";
        if(confidence<0.75) return "medium";        
        return "hög";
    }
    
}
