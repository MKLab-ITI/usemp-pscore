/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gpetkos
 */
public class Dimension {
    private String dimension_name;
    private Double dimension_privacy_score;
    private Double dimension_visibility_overall;
    private Double dimension_level_of_control;
    private Constants.VisibilityLabel dimension_visibility_label;
    private Integer dimension_visibility_actual_audience;
    private Double dimension_sensitivity;
//    private Map<String,Attribute> attributes;
    private List<Attribute> attributes;

    public Dimension(String dimension_name) {
        this.dimension_name = dimension_name;
//        attributes=new HashMap<String,Attribute>();
        attributes=new ArrayList<Attribute>();
    }
    
    public Dimension() {
//        attributes=new HashMap<String,Attribute>();
        attributes=new ArrayList<Attribute>();
    }

    
    public Attribute addAttribute(String attribute_name, Boolean normalized){
//        Attribute attribute=attributes.get(attribute_name);
        Attribute attribute=null;
        for(Attribute attributeT:attributes)
            if(attributeT.getAttribute_name().equals(attribute_name)){
                attribute=attributeT;
                break;
            }
                
        
        if(attribute==null){
            attribute=new Attribute(normalized,attribute_name);
        }
//        attributes.put(attribute_name, attribute);
        attributes.add(attribute);
        return attribute;
    }
    
    
    public Attribute getAttribute(String attribute_name){
//        return attributes.get(attribute_name);
        Attribute attribute=null;
        for(Attribute attributeT:attributes)
            if(attributeT.getAttribute_name().equals(attribute_name)){
                attribute=attributeT;
                break;
            }
        return attribute;
    }

    public String getDimension_name() {
        return dimension_name;
    }

    public Double getDimension_privacy_score() {
        return dimension_privacy_score;
    }

    public Double getDimension_visibility_overall() {
        return dimension_visibility_overall;
    }

    public Double getDimension_level_of_control() {
        return dimension_level_of_control;
    }

    public Constants.VisibilityLabel getDimension_visibility_label() {
        return dimension_visibility_label;
    }

    public Integer getDimension_visibility_actual_audience() {
        return dimension_visibility_actual_audience;
    }

    public Double getDimension_sensitivity() {
        return dimension_sensitivity;
    }

    /*
    public Map<String, Attribute> getAttributes() {
        return attributes;
    }
    */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setSensitivity(Double dimension_sensitivity) {
        this.dimension_sensitivity = dimension_sensitivity;
    }
    
    public void setSensitivityCascade(Double sensitivity){
        dimension_sensitivity=sensitivity;
        for(Attribute attribute:attributes) attribute.setSensitivityCascade(sensitivity);
    }

    public void computeScores(){
        for(Attribute attribute:attributes)
            attribute.computeScores();
        aggregatePrivacyScores(attributes);
        aggregateVisibilityOverall(attributes);
        aggregateLevelOfControl(attributes);
        //aggregateLevelOfControl(attributes.values());
        aggregateVisibilityLabel(attributes);
        }
    
    private void aggregatePrivacyScores(Collection<Attribute> attributes){
        Double result=0.0;
        for(Attribute attribute:attributes)
            result=result+attribute.getAttribute_privacy_score();
        result=result/attributes.size();
        dimension_privacy_score=result;
    }
    
    private void aggregateVisibilityOverall(Collection<Attribute> attributes){
        Double result=0.0;
        for(Attribute attribute:attributes)
            result=result+attribute.getAttribute_visibility_overall();
        result=result/attributes.size();
        this.dimension_visibility_overall=result;
    }

    private void aggregateLevelOfControl(Collection<Attribute> attributes){
        Double result=Double.MAX_VALUE;
        for(Attribute attribute:attributes){
            if(attribute==null) System.out.println("Attribute is null");
            if(attribute.getAttribute_level_of_control()==null) System.out.println("Attribute level of control is null");
            if(attribute.getAttribute_level_of_control()<result)
                result=attribute.getAttribute_level_of_control();
        }
        this.dimension_level_of_control=result;
    }
    
    private void aggregateVisibilityLabel(Collection<Attribute> attributes){
        Constants.VisibilityLabel result=Constants.VisibilityLabel.SELF;
        for(Attribute attribute:attributes)
            if(attribute.getAttribute_visibility_label().compareTo(result)<0)
                result=attribute.getAttribute_visibility_label();
        this.dimension_visibility_label=result;
    }
    
}
