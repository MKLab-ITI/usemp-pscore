/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gpetkos
 */
public class ScoringUser {
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

    public void addSupport(String dimension_name,String attribute_name, String value_name,List<String> ids,Double confidence, Constants.InferenceMechanism inferenceMechanism){
        /*
            Support support=new Support();
            support.setSupport_confidence(confidence);
            support.setSupport_inference_mechanism(inferenceMechanism);
            support.setSupport_data_pointer_id(ids);
        */
//        Dimension dimension=dimensions.get(dimension_name);
        Dimension dimension=null;
        for(int i=0;i<dimensions.size();i++) 
            if (dimensions.get(i).getDimension_name().equals(dimension_name)){
                dimension=dimensions.get(i);
                break;
            }

        if(dimension==null) {
//            System.out.println("Dimension is null");
            return;
        }
        Attribute attribute=dimension.getAttribute(attribute_name);
        if(attribute==null){
//            System.out.println("Attribute is null");
            return;
        }
        Value value=attribute.getValue(value_name);
        if(value==null){
//            System.out.println("Value is null");
            return;
        }
        value.addSupport(inferenceMechanism, ids, confidence, user_id);
        
    }
    
}
