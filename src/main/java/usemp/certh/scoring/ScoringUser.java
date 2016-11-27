package usemp.certh.scoring;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import usemp.certh.userDataAccess.UserDataAccess;
import com.restfb.types.Page;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import java.util.Map.Entry;
import usemp.certh.inference.preprepilot.Classification;
import usemp.certh.inference.preprepilot.PrePilotClassifier;

/**
 *
 * @author gpetkos
 * 
 * This class represents the complete scores for a user.
 * 
 */
public class ScoringUser {
//    @Id
    private String user_id;
    private Double user_level_of_control;
    private Double user_privacy_score;
    private Double user_visibility_overall;
    private Constants.VisibilityLabel user_visibility_label;
    private Integer user_visibility_actual_audience;
    private Double overall_personal_data_value;
    private Double user_influence;
    private Map<Item,Double> personal_data_value_per_item;
    //private Map<String,Dimension> dimensions;
    private List<Dimension> dimensions;

    public ScoringUser(String user_id) {
        this.user_id = user_id;
        personal_data_value_per_item=new HashMap<Item,Double>();
//        dimensions=new HashMap<String,Dimension>();
        dimensions=new ArrayList<Dimension>();
    }

    public ScoringUser(){
        dimensions=new ArrayList<Dimension>();
    }
    

    public ScoringUser(ScoringUser scoringUser){
        user_id=scoringUser.user_id;
        user_level_of_control=scoringUser.user_level_of_control;
        user_privacy_score=scoringUser.user_privacy_score;
        user_visibility_overall=scoringUser.user_visibility_overall;
        user_visibility_label=scoringUser.user_visibility_label;
        user_visibility_actual_audience=scoringUser.user_visibility_actual_audience;
        overall_personal_data_value=scoringUser.overall_personal_data_value;
        user_influence=scoringUser.user_influence;
        personal_data_value_per_item=new HashMap<Item,Double>();
        dimensions=new ArrayList<Dimension>();
        for(Dimension dimension:scoringUser.getDimensions()){
            Dimension newDimension=new Dimension(dimension);
            dimensions.add(newDimension);
        }
    }
    
    
    public Dimension addDimension(String dimension_name, Double sensitivity){
        Dimension dimension=null;
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }
        if(dimension==null){
            dimension=new Dimension(dimension_name);
            if(sensitivity==null){
                Double defSensitivity=Constants.defaultSensitivities.get(dimension_name);
                if(defSensitivity!=null) dimension.setSensitivityCascade(defSensitivity);
            }
            else
                dimension.setSensitivityCascade(sensitivity);
//            dimensions.put(dimension_name, dimension);
            dimensions.add(dimension);
        }
        return dimension;
    }

    public Attribute addAttribute(String dimension_name,String attribute_name, Boolean normalized,Double sensitivity){
        Dimension dimension=null;
//        Dimension dimension=dimensions.get(dimension_name);
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }
        if(dimension==null){
            dimension=addDimension(dimension_name, sensitivity);
//            dimension=new Dimension(dimension_name);
//            dimensions.put(dimension_name, dimension);
        }
        Attribute attribute=dimension.getAttribute(attribute_name);
        if(attribute==null){
            attribute=dimension.addAttribute(attribute_name, normalized);
            Double sensAtt=sensitivity;
            if(sensitivity!=null)
                attribute.setSensitivityCascade(sensitivity);
            else
                attribute.setSensitivityCascade(dimension.getDimension_sensitivity());
        }
        return attribute;
    }

    public Value addValue(String dimension_name,String attribute_name, Boolean attribute_normalized,String value,Double sensitivity){
        addAttribute(dimension_name, attribute_name, attribute_normalized, sensitivity);
//        Dimension dimension=dimensions.get(dimension_name);
        Dimension dimension=null;
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }
        Attribute attribute=dimension.getAttribute(attribute_name);
        Value valueObj=attribute.getValue(value);
        if(valueObj==null){
            valueObj=attribute.addValue(value);
            Double sensVal=sensitivity;
            if(sensitivity!=null)
                valueObj.setSensitivity(sensitivity);
            else
                valueObj.setSensitivity(attribute.getAttribute_sensitivity());
        }
        return valueObj;
    }

    public String getUser_id() {
        return user_id;
    }

    public Double getUser_level_of_control() {
        return user_level_of_control;
    }

    public Double getUser_privacy_score() {
        return user_privacy_score;
    }

    public Double getUser_visibility_overall() {
        return user_visibility_overall;
    }

    public Constants.VisibilityLabel getUser_visibility_label() {
        return user_visibility_label;
    }

    public Integer getUser_visibility_actual_audience() {
        return user_visibility_actual_audience;
    }

    public Double getOverall_personal_data_value() {
        return overall_personal_data_value;
    }

    public Double getUser_influence() {
        return user_influence;
    }

    public Map<Item, Double> getPersonal_data_value_per_item() {
        return personal_data_value_per_item;
    }

    /*
    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }
    */
    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public String toJSonStringPretty(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convert object to JSON string
            //String jsonInString = mapper.writeValueAsString(user);
            //System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
        return null;
    }

    public String toJSonString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convert object to JSON string
            return mapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
        return null;
    }
    
    public Dimension getDimension(String dimension_name){
//        return dimensions.get(dimension_name);
        Dimension dimension=null;
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }
        return dimension;
    }
    
    public void setSensitivityCascade(Double sensitivity){
//        for(Dimension dimension:dimensions.values())
        for(Dimension dimension:dimensions)
            dimension.setSensitivityCascade(sensitivity);
        computeScores();
    }
    
    public boolean setDimensionSensitivity(String dimension, Double sensitivity) {
        Dimension dim = getDimension(dimension);
        if (dim == null){
            return false;
        }
        dim.setSensitivityCascade(sensitivity);
        computeScores();
        return true;
    }

    public boolean setAttributeSensitivity(String dimension, String attribute, Double sensitivity) {
        Dimension dim = getDimension(dimension);
        if (dim == null){
            return false;
        }
        Attribute att = dim.getAttribute(attribute);
        if (att == null){
            return false;
        }
        att.setSensitivityCascade(sensitivity);
        computeScores();
        return true;
    }

    public boolean setValueSensitivity(String dimension, String attribute, String value, Double sensitivity) {
        Dimension dim = getDimension(dimension);
        if (dim == null){
            return false;
        }
        Attribute att = dim.getAttribute(attribute);
        if (att == null){
            return false;
        }
        Value val = att.getValue(value);
        if (val == null){
            return false;
        }
        val.setSensitivity(sensitivity);
        computeScores();
        return true;
    }
    
    
    public void computeScores(){
        for(Dimension dimension:dimensions)
            dimension.computeScores();
        aggregatePrivacyScores(dimensions);
        aggregateVisibilityOverall(dimensions);
        aggregateLevelOfControl(dimensions);
        aggregateLevelOfControl(dimensions);
        aggregateVisibilityLabel(dimensions);
    }
    
    private void aggregatePrivacyScores(Collection<Dimension> dimensions){
        Double result=0.0;
        for(Dimension dimension:dimensions)
            result=result+dimension.getDimension_privacy_score();
        result=result/dimensions.size();
        user_privacy_score=result;
    }
    
    private void aggregateVisibilityOverall(Collection<Dimension> dimensions){
        Double result=0.0;
        for(Dimension dimension:dimensions)
            result=result+dimension.getDimension_visibility_overall();
        result=result/dimensions.size();
        this.user_visibility_overall=result;
    }

    private void aggregateLevelOfControl(Collection<Dimension> dimensions){
        Double result=Double.MAX_VALUE;
        for(Dimension dimension:dimensions)
            if(dimension.getDimension_level_of_control()<result)
                result=dimension.getDimension_level_of_control();
        this.user_level_of_control=result;
    }
    
    private void aggregateVisibilityLabel(Collection<Dimension> dimensions){
        Constants.VisibilityLabel result=Constants.VisibilityLabel.SELF;
        for(Dimension dimension:dimensions)
            if(dimension.getDimension_visibility_label().compareTo(result)<0)
                result=dimension.getDimension_visibility_label();
        this.user_visibility_label=result;
    }

    public void deleteData(ArrayList<String> dataToDelete){
        for(String datumToDelete:dataToDelete){
            
            String datumToDeleteWildcard="";
            if(datumToDelete.startsWith("LIKE")) datumToDeleteWildcard="LIKE *";
            if(datumToDelete.startsWith("POST")) datumToDeleteWildcard="POST *";
            if(datumToDelete.startsWith("IMAGE")) datumToDeleteWildcard="IMAGE *";
            for(Dimension dimension:dimensions){
                for(Attribute attribute:dimension.getAttributes()){
                    for(Value value:attribute.getValues()){
                        Set<Support> supportsForDeletion=new HashSet<Support>();
                        for(Support support:value.getSupports()){
                            List<String> pointers=support.getSupport_data_pointer_ids();
                            for(String pointer:pointers){
                                if((pointer.equals(datumToDelete))||(pointer.equals(datumToDeleteWildcard)))
                                        supportsForDeletion.add(support);
                            }
                        }
                        value.getSupports().removeAll(supportsForDeletion);
                    }
                }
            }
        }
        computeScores();
        
    }
    

    public void addSupport(String dimension_name,String attribute_name, String value_name,List<String> ids,Double confidence, Constants.InferenceMechanism inferenceMechanism,String description_en,String description_du, String description_sw, UserDataAccess user_data){
        Dimension dimension=null;
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }

        if(dimension==null) {
            dimension=addDimension(dimension_name,null);
        }
        Attribute attribute=dimension.getAttribute(attribute_name);
        if(attribute==null){
            attribute=addAttribute(dimension_name,attribute_name,true,null);
//            System.out.println("Attribute is null");
        }
        
        Value value=attribute.getValue(value_name);
        if(value==null){
            value=addValue(dimension_name,attribute_name, false,value_name,null);
//            System.out.println("Value is null");
        }
        value.addSupport(inferenceMechanism, ids, confidence, user_id,description_en,description_du, description_sw, user_data);
        computeScores();
    }

    
    public static Double remapScore(Double inScore){
        Double result=0.0;
        Double limit1=0.15;
        Double limit2=0.35;
        if(inScore<limit1){
            result=((limit1-inScore)/limit1)*0.3;
            return result;
        }
        if(inScore<limit2){
            result=((limit2-inScore)/(limit2-limit1))*0.4+0.3;
            return result;
        }
        result=((1.0-inScore)/(1.0-limit2))*0.3+0.7;
        return result;
    }
    
    public static NumberFormat formatter = new DecimalFormat("#0.00");   

    
    
    public ControlSuggestionSet computeControlSuggestionSet(UserDataAccess userData){
        ControlSuggestionSet css=new ControlSuggestionSet(user_id);
        HashSet<Page> userlikes=userData.getAllLikes();

//        UserLikes userLikesObj=fbData.getMongoOperation().findById(user_id, UserLikes.class);
//        List<Category> userlikes = null;
//        if(userLikesObj!=null)
//            userlikes =userLikesObj.getLikes();
        
        for(Dimension dimension:dimensions){
            for(Attribute attribute:dimension.getAttributes()){
                for(Value value:attribute.getValues()){
                    for(Support support:value.getSupports()){
                        if(support.getSupport_inference_mechanism()==Constants.InferenceMechanism.LIKES_MAPPING){
                            for(String id:support.getSupport_data_pointer_ids()){
                                String id_s=id;
                                id_s=id_s.replace("LIKE","");
                                id_s=id_s.trim();
                                Double confidence=support.getSupport_confidence();
                                if(confidence==null) confidence=1.0;
                                Double sensitivity=value.getValue_sensitivity();
                                if(sensitivity==null) sensitivity=1.0;
                                Double visibility=support.getSupport_visibility();
                                if(visibility==null) visibility=1.0;
                                Double score=sensitivity*visibility*confidence;
                                String likeName=null;
                                if(userlikes!=null){
                                    for(Page category:userlikes)
                                        if(category.getId().equals(id_s)){
                                            likeName=category.getName();
                                            break;
                                        }
                                }
                                /*
                                if(likeName==null){
                                    Like like = fbData.getLikeDescription(id_s);
                                    likeName=like.getName();
                                }
                                */
//                                String description_en="This like (<a href=\"https://www.facebook.com/"+id_s+"/\">"+like.getName()+"</a>) is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)..";
//                                String description_du="Deze vind-ik-leuk (<a href=\"https://www.facebook.com/"+id_s+"/\">"+like.getName()+"</a>) wordt geassocieerd met "+Translator.translateDu(dimension.getDimension_name()) +" &rarr; "+Translator.translateDu(attribute.getAttribute_name())+" (onthullingsscore: "+formatter.format(score*100)+"%)";
//                                String description_sw="Denna gilla-markering (<a href=\"https://www.facebook.com/"+id_s+"/\">"+like.getName()+"</a>) hör ihop med "+Translator.translateSw(dimension.getDimension_name())+" &rarr; "+Translator.translateSw(attribute.getAttribute_name())+" (avslöjandegrad: "+formatter.format(score*100)+"%)";
                                String description_en="The like (<a href=\"https://www.facebook.com/"+id_s+"/\">"+likeName+"</a>) is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)..";
                                String description_du="The like (<a href=\"https://www.facebook.com/"+id_s+"/\">"+likeName+"</a>) is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)..";
                                String description_sw="The like (<a href=\"https://www.facebook.com/"+id_s+"/\">"+likeName+"</a>) is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)..";
//                                String description_du="Deze vind-ik-leuk (<a href=\"https://www.facebook.com/"+id_s+"/\">"+likeName+"</a>) wordt geassocieerd met "+Translator.translateDu(dimension.getDimension_name()) +" &rarr; "+Translator.translateDu(attribute.getAttribute_name())+" (onthullingsscore: "+formatter.format(score*100)+"%)";
//                                String description_sw="Denna gilla-markering (<a href=\"https://www.facebook.com/"+id_s+"/\">"+likeName+"</a>) hör ihop med "+Translator.translateSw(dimension.getDimension_name())+" &rarr; "+Translator.translateSw(attribute.getAttribute_name())+" (avslöjandegrad: "+formatter.format(score*100)+"%)";
                                ControlSuggestion new_cs=new ControlSuggestion(id_s,dimension.getDimension_name(),attribute.getAttribute_name(),value.getValue_name(),confidence,score,description_en,description_du,description_sw);
                                css.addControlSuggestionLike(new_cs);
                            }
                        }
                        if(support.getSupport_inference_mechanism()==Constants.InferenceMechanism.URLS_MAPPING){
                            for(String id:support.getSupport_data_pointer_ids()){
                                String id_s=id;
                                id_s=id_s.replace("POST","");
                                id_s=id_s.trim();
                                Double confidence=support.getSupport_confidence();
                                if(confidence==null) confidence=1.0;
                                Double sensitivity=value.getValue_sensitivity();
                                if(sensitivity==null) sensitivity=1.0;
                                Double visibility=support.getSupport_visibility();
                                if(visibility==null) visibility=1.0;
                                Double score=sensitivity*visibility*confidence;
                                String description_en="The URL included in <a href=\""+userData.getPost(id_s).getLink()+"\">this post</a> is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
                                String description_du="The URL included in <a href=\""+userData.getPost(id_s).getLink()+"\">this post</a> is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
                                String description_sw="The URL included in <a href=\""+userData.getPost(id_s).getLink()+"\">this post</a> is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
//                                String description_du="The URL included in <a href=\""+userData.getPost(id_s).getLink()+"\">this post</a> is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
//                                String description_sw="The URL included in <a href=\""+userData.getPost(id_s).getLink()+"\">this post</a> is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
                                
//                                String description_du="De URL in deze post wordt geassocieerd met "+Translator.translateDu(dimension.getDimension_name()) +" &rarr; "+Translator.translateDu(attribute.getAttribute_name())+" (onthullingsscore: "+formatter.format(score*100)+"%)";
//                                String description_sw="Web-adressen i det här inlägget hör ihop med "+Translator.translateSw(dimension.getDimension_name())+" &rarr; "+Translator.translateSw(attribute.getAttribute_name())+" (avslöjandegrad: "+formatter.format(score*100)+"%)";
                                ControlSuggestion new_cs=new ControlSuggestion(id_s,dimension.getDimension_name(),attribute.getAttribute_name(),value.getValue_name(),confidence,score,description_en,description_du,description_sw);
                                css.addControlSuggestionPost(new_cs);
                            }
                        }
                        if(support.getSupport_inference_mechanism()==Constants.InferenceMechanism.VISUAL_CONCEPTS_MAPPING){
                            for(String id:support.getSupport_data_pointer_ids()){
                                String id_s=id;
                                id_s=id_s.replace("IMAGE","");
                                id_s=id_s.trim();
                                Double confidence=support.getSupport_confidence();
                                if(confidence==null) confidence=1.0;
                                Double sensitivity=value.getValue_sensitivity();
                                if(sensitivity==null) sensitivity=1.0;
                                Double visibility=support.getSupport_visibility();
                                if(visibility==null) visibility=1.0;
                                Double score=sensitivity*visibility*confidence;
                                String description_en="The content of this image is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
                                String description_du="The content of this image is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
                                String description_sw="The content of this image is associated to "+dimension.getDimension_name()+" &rarr; "+attribute.getAttribute_name()+" (disclosure score: "+formatter.format(score*100)+"%)";
//                                String description_du="Deze image wordt geassocieerd met "+Translator.translateDu(dimension.getDimension_name()) +" &rarr; "+Translator.translateDu(attribute.getAttribute_name())+" (onthullingsscore: "+formatter.format(score*100)+"%)";
//                                String description_sw="Här inlägget hör ihop med "+Translator.translateSw(dimension.getDimension_name())+" &rarr; "+Translator.translateSw(attribute.getAttribute_name())+" (avslöjandegrad: "+formatter.format(score*100)+"%)";
                                ControlSuggestion new_cs=new ControlSuggestion(id_s,dimension.getDimension_name(),attribute.getAttribute_name(),value.getValue_name(),confidence,score,description_en,description_du,description_sw);
                                String url=null;
                                //get urls from mongo
                                //Photo photo=fbData.getMongoOperation().findById(id_s, Photo.class);
                                //if(photo!=null) url=photo.getPicture();
                                //new_cs.setPointer(url);
                                new_cs.setPointer(id_s);
                                
                                css.addControlSuggestionImage(new_cs);
                            }
                        }
                    }
                }
            }
        }
        css.orderAll();
        return css;
    }

    public ControlSuggestionSetExtended computeControlSuggestionSetExtended(UserDataAccess userData, PrePilotClassifier ppc){
        ControlSuggestionSetExtended css=new ControlSuggestionSetExtended(user_id);
        HashSet<Page> userlikes=userData.getAllLikes();

        ScoringUser scoringUser=userData.getScoringUser();
        Classification ucd = ppc.loadClassificationData(userData);
        
        HashMap<String,Double> initialScores=new HashMap<String,Double>();
        initialScores.put("Overall",scoringUser.getUser_privacy_score());
        for(Dimension dimension:scoringUser.getDimensions()){
            initialScores.put(dimension.getDimension_name(),dimension.getDimension_privacy_score());
        }
        
        //Then loop through the posts and examine how the score changes
        for(Post post:userData.getAllPosts()){
            //First copy the actual scores:
            ScoringUser scoringUserTmp=new ScoringUser(scoringUser);
            userData.setScoringUser(scoringUserTmp);
            //First remove the message from the classification data
            if(post.getMessage()!=null){
                ucd.removePost(post);
                //And remove any supports that contain it
                ArrayList<String> dataToDelete=new ArrayList<String>();
                dataToDelete.add("POST "+post.getId());
                scoringUserTmp.deleteData(dataToDelete);

                //Then re-run the classifiers
                //First the prepilot classifier
                ppc.classifyAll(ucd, userData);

                //Finally, add the message again to the classification data, so that it is used in the following prepilot classifications
                ucd.addPost(post);

                String _id=post.getId();
                for(Dimension dimension:scoringUserTmp.getDimensions()){
                    String _dimension=dimension.getDimension_name();
                    String _attribute="";
                    String _value="";
                    Double _confidence=0.0;
                    Double newScore=dimension.getDimension_privacy_score();
                    Double initialScore=initialScores.get(dimension.getDimension_name());
                    Double _score=initialScore-newScore;
                    String description_en="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    String description_du="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    String description_sw="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    //String description_du="This post contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    //String description_sw="This post contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    if(_score>0){
                        ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                        css.addControlSuggestionPost(new_cs);
                    }                    
                }
                String _dimension="Overall";
                String _attribute="";
                String _value="";
                Double _confidence=0.0;
                Double newScore=scoringUserTmp.getUser_privacy_score();
                Double initialScore=initialScores.get("Overall");
                Double _score=initialScore-newScore;
                String description_en="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" of the " +formatter.format(initialScore*100)+"% disclosure score";
                String description_du="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" of the " +formatter.format(initialScore*100)+"% disclosure score";
                String description_sw="The URL included in <a href=\""+userData.getPost(_id).getActions().get(0).getLink()+"\">this post</a> contributes "+formatter.format(_score*100)+" of the " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_en="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_du="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_sw="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                if(_score>0){
                    ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                    css.addControlSuggestionPost(new_cs);
                }
            }
            userData.setScoringUser(scoringUser);
        }

        //Then loop through the statuses and examine how the score changes
        for(StatusMessage status:userData.getAllStatuses()){
            //First copy the actual scores:
            ScoringUser scoringUserTmp=new ScoringUser(scoringUser);
            userData.setScoringUser(scoringUserTmp);

            //First remove the message from the classification data
            if(status.getMessage()!=null){
                ucd.removeStatus(status);
                //And remove any supports that contain it
                ArrayList<String> dataToDelete=new ArrayList<String>();
                dataToDelete.add("POST "+status.getId());
                scoringUserTmp.deleteData(dataToDelete);

                //Then re-run the classifiers
                //First the prepilot classifier
                ppc.classifyAll(ucd, userData);

                //Finally, add the message again to the classification data, so that it is used in the following prepilot classifications
                ucd.addStatus(status);

                String _id=status.getId();
                for(Dimension dimension:scoringUserTmp.getDimensions()){
                    String _dimension=dimension.getDimension_name();
                    String _attribute="";
                    String _value="";
                    Double _confidence=0.0;
                    Double newScore=dimension.getDimension_privacy_score();
                    Double initialScore=initialScores.get(dimension.getDimension_name());
                    Double _score=initialScore-newScore;
                    String description_en="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    String description_du="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
                    String description_sw="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension "+_dimension+"'";
//                    String description_en="The post contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
//                    String description_du="The post contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
//                    String description_sw="The post contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                    if(_score>0){
                        ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                        css.addControlSuggestionPost(new_cs);
                    }                    
                }
                String _dimension="Overall";
                String _attribute="";
                String _value="";
                Double _confidence=0.0;
                Double newScore=scoringUserTmp.getUser_privacy_score();
                Double initialScore=initialScores.get("Overall");
                Double _score=initialScore-newScore;
                String description_en="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                String description_du="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                String description_sw="The URL included in <a href=\"https://www.facebook.com/"+userData.getStatus(_id).getFrom().getId()+"/posts/"+_id+"\">this post</a> contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_en="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_du="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_sw="This post contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                if(_score>0){
                    ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                    css.addControlSuggestionPost(new_cs);
                }
            }
            userData.setScoringUser(scoringUser);
        }

        //Then loop through the likes and examine how the score changes
        for(Page page:userData.getAllLikes()){
            //First copy the actual scores:
            ScoringUser scoringUserTmp=new ScoringUser(scoringUser);
            userData.setScoringUser(scoringUserTmp);

            //First remove the message from the classification data
            if(page!=null){
                ucd.removeLike(page);
                //And remove any supports that contain it
                ArrayList<String> dataToDelete=new ArrayList<String>();
                dataToDelete.add("LIKE "+page.getId());
                scoringUserTmp.deleteData(dataToDelete);

                //Then re-run the classifiers
                //First the prepilot classifier
                ppc.classifyAll(ucd, userData);

                //Finally, add the message again to the classification data, so that it is used in the following prepilot classifications
                ucd.addLike(page);

                String _id=page.getId();
                for(Dimension dimension:scoringUserTmp.getDimensions()){
                    String _dimension=dimension.getDimension_name();
                    String _attribute="";
                    String _value="";
                    Double _confidence=0.0;
                    Double newScore=dimension.getDimension_privacy_score();
                    Double initialScore=initialScores.get(dimension.getDimension_name());
                    Double _score=initialScore-newScore;
                    String likeName=page.getName();
                    String description_en="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                    String description_du="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                    String description_sw="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
//                    String description_en="This like contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
//                    String description_du="This like contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
//                    String description_sw="This like contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                    if(_score>0){
                        ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                        css.addControlSuggestionLike(new_cs);
                    }                    
                }
                String _dimension="Overall";
                String _attribute="";
                String _value="";
                Double _confidence=0.0;
                Double newScore=scoringUserTmp.getUser_privacy_score();
                Double initialScore=initialScores.get("Overall");
                Double _score=initialScore-newScore;
                String likeName=page.getName();
                String description_en="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                String description_du="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                String description_sw="The like (<a href=\"https://www.facebook.com/"+_id+"/\">"+likeName+"</a>) contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_en="This like contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_du="This like contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
//                String description_sw="This like contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                if(_score>0){
                    ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                    css.addControlSuggestionLike(new_cs);
                }
            }
            userData.setScoringUser(scoringUser);
        }

        //Finally, loop through the images and examine how the score changes
        for(Photo photo:userData.getAllPhotos()){
            //First copy the actual scores:
            ScoringUser scoringUserTmp=new ScoringUser(scoringUser);
            userData.setScoringUser(scoringUserTmp);

            //First remove the image from the classification data
            if(photo!=null){
                HashMap<String,Double> photoConcepts=userData.getPhotoConcepts(photo.getId());
                if(photoConcepts!=null){
                    for(Entry<String,Double> ent:photoConcepts.entrySet()){
                        ucd.removeConcept(ent.getKey(), ent.getValue());
                    }
                    //And remove any supports that contain it
                    ArrayList<String> dataToDelete=new ArrayList<String>();
                    dataToDelete.add("IMAGE "+photo.getId());
                    scoringUserTmp.deleteData(dataToDelete);

                    //Then re-run the classifiers
                    //First the prepilot classifier
                    ppc.classifyAll(ucd, userData);

                    //Finally, add the message again to the classification data, so that it is used in the following prepilot classifications
                    //ucd.addLike(page);
                    for(Entry<String,Double> ent:photoConcepts.entrySet()){
                        ucd.addConcept(ent.getKey(), ent.getValue());
                    }

//                    String _id=photo.getId();
                    String _id=userData.getImageFilemane(photo.getId());
                    for(Dimension dimension:scoringUserTmp.getDimensions()){
                        String _dimension=dimension.getDimension_name();
                        String _attribute="";
                        String _value="";
                        Double _confidence=0.0;
                        Double newScore=dimension.getDimension_privacy_score();
                        Double initialScore=initialScores.get(dimension.getDimension_name());
                        Double _score=initialScore-newScore;
                        String description_en="This image contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                        String description_du="This image contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                        String description_sw="This image contributes "+formatter.format(_score*100)+" out of the " +formatter.format(initialScore*100)+"% disclosure score of the dimension '"+_dimension+"'";
                        if(_score>0){
                            ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                            css.addControlSuggestionImage(new_cs);
                        }                    
                    }
                    String _dimension="Overall";
                    String _attribute="";
                    String _value="";
                    Double _confidence=0.0;
                    Double newScore=scoringUserTmp.getUser_privacy_score();
                    Double initialScore=initialScores.get("Overall");
                    Double _score=initialScore-newScore;
                    String description_en="This image contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                    String description_du="This image contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                    String description_sw="This image contributes "+formatter.format(_score*100)+" out of the overall " +formatter.format(initialScore*100)+"% disclosure score";
                    if(_score>0){
                        ControlSuggestion new_cs=new ControlSuggestion(_id, _dimension, _attribute, _value, _confidence, _score, description_en, description_du, description_sw);
                        css.addControlSuggestionImage(new_cs);
                    }
                }
            }
            userData.setScoringUser(scoringUser);
        }
        
        
        css.orderAll();
        return css;
    }
    
    
}
