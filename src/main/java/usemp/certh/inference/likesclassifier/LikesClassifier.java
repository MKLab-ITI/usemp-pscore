package usemp.certh.inference.likesclassifier;

import com.restfb.types.Page;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import usemp.certh.userDataAccess.UserDataAccess;
import usemp.certh.scoring.Constants;
import usemp.certh.scoring.ScoringUser;

/**
 *
 * @author gpetkos
 * 
 * This class is the implementation of an inference module that examines the 
 * liked pages of a user and associates them to specific privacy attributes.
 * For instance, liking the page of Starbucks can be associated to the 
 * attribute "coffee" (under the "Health" dimension)
 * 
 */
public class LikesClassifier {
    

    private Double defaultConfidence=0.8;
    private HashMap<String,String> likesMapping;
    private HashMap<String,String> catsMapping;
    
    public LikesClassifier() {
        BufferedReader in;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("likes_mapping.txt"), "UTF8"));
            String line;
            likesMapping=new HashMap<String,String>();
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                likesMapping.put(parts[0], parts[1]);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Mapping from like ids to privacy values loaded!");

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("likesCategoriesMapping.txt"), "UTF8"));
            catsMapping=new HashMap<String,String>();
            String line="";
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                catsMapping.put(parts[0], parts[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Mapping from like categories to privacy values loaded!");
    }

    public void classify(UserDataAccess user_data) {
        ScoringUser scoringUser=user_data.getScoringUser();
        HashSet<Page> userLikes=user_data.getAllLikes();
        if(userLikes==null) return;

        HashMap<String,HashMap<String,HashSet<String>>> counts=new HashMap<String,HashMap<String,HashSet<String>>>();
        
        for (Page l : userLikes) {
            String likeId = l.getId();
            String privacyValue=likesMapping.get(likeId);
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
                lks.add(likeId);
            }
            //Here we check the like's category.
            //We only do this if there is no mapping according to the like id.
            else{
                    String like_category=l.getCategory();
                    //System.out.println("LIKE CATEGORY: "+like_category);
                    privacyValue=catsMapping.get(like_category);
                    if(privacyValue!=null){
//                            System.out.println("LIKES CATEGORY ASSSOCIATION!!!!!");
//                            System.out.println("VVV: "+privacyValue);
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
                        lks.add(likeId);
                    }
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
                    String description_en="These results have been associated with "+Constants.confidence_en(defaultConfidence)+" confidence to the following pages that you have liked:  ";
                    String description_du="Deze resultaten werden geassocieerd met de pagina’s die je leuk vond met een "+Constants.confidence_du(defaultConfidence)+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(defaultConfidence)+" konfidens på följande sidor som du har gillat: ";
                    
                    for(String likeId:ent.getValue()){
                        pointersToData.add("LIKE "+likeId);
                        Page like=user_data.getLike(likeId);
                        if(like!=null){
                            description_en=description_en+like.getName()+", ";
                            description_du=description_du+like.getName()+", ";
                            description_sw=description_sw+like.getName()+", ";
                        }
                        else{
                            System.out.println("ALERT!!!!!!");
                            description_en=description_en+likeId+" (this is the Facebook id of the page, sorry its title is not available), ";
                            description_du=description_du+likeId+" (this is the Facebook id of the page, sorry its title is not available), ";
                            description_sw=description_sw+likeId+" (detta är sidans facebook-id, tyvärr är titeln inte tillgänglig), ";
                        }
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, defaultConfidence,
                            usemp.certh.scoring.Constants.InferenceMechanism.LIKES_MAPPING,description_en,description_du,description_sw, user_data);
                }
            }
            else{
                for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                    List<String> pointersToData=new ArrayList<String>();
//                    String description="Because you liked the following pages: ";
//                    String description_en="This value has been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following pages that you have liked:  ";
//                    String description_du="(du) This value has been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following pages that you have liked: ";
//                    String description_sw="(sw) This value has been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following pages that you have liked: ";
                    String description_en="These results have been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following pages that you have liked:  ";
                    String description_du="Deze resultaten werden geassocieerd met de pagina’s die je leuk vond met een "+Constants.confidence_du(ent.getValue().size()/totalCount)+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(ent.getValue().size()/totalCount)+" konfidens på följande sidor som du har gillat: ";
                    
                    for(String likeId:ent.getValue()){
                        pointersToData.add("LIKE "+likeId);
                        Page like=user_data.getLike(likeId);
                        if(like!=null){
                            description_en=description_en+like.getName()+", ";
                            description_du=description_du+like.getName()+", ";
                            description_sw=description_sw+like.getName()+", ";
                        }
                        else{
                            System.out.println("ALERT!!!!!!");
                            description_en=description_en+likeId+" (this is the Facebook id of the page, sorry its title is not available), ";
                            description_du=description_du+likeId+" (this is the Facebook id of the page, sorry its title is not available), ";
                            description_sw=description_sw+likeId+" (detta är sidans facebook-id, tyvärr är titeln inte tillgänglig), ";
                        }
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, ent.getValue().size()/totalCount,
                            usemp.certh.scoring.Constants.InferenceMechanism.LIKES_MAPPING,description_en,description_du,description_sw,user_data);
                }
            }
        }
    }
    
}
