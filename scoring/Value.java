/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author gpetkos
 */

public class Value {
    private String value_name;
    private Double value_confidence;
    private Constants.VisibilityLabel value_visibility_label;
    private Double value_sensitivity;
    private Integer value_visibility_actual_audience;
    private Double value_level_of_control;
    private Double value_visibility_overall;
    private Double value_privacy_score;
    private Boolean value_is_inferred;
    private List<Support> supports;

    public Value(String value_name) {
        this.value_name = value_name;
        value_is_inferred=true;
        supports=new ArrayList<Support>();
    }

    public Value() {
        supports=new ArrayList<Support>();
        value_is_inferred=true;
    }
    
    
    public void addSupport(Constants.InferenceMechanism support_inference_mechanism, List<String> support_data_pointer_ids, Double support_confidence, String user_id){
        Support support=new Support(support_inference_mechanism, support_data_pointer_ids, support_confidence,user_id);
        supports.add(support);
//        System.out.println("ADDED SUPPORT :"+support_confidence);
    }

    public String getValue_name() {
        return value_name;
    }

    public Double getValue_confidence() {
        return value_confidence;
    }

    public Constants.VisibilityLabel getValue_visibility_label() {
        return value_visibility_label;
    }

    public Double getValue_sensitivity() {
        return value_sensitivity;
    }

    public Integer getValue_visibility_actual_audience() {
        return value_visibility_actual_audience;
    }

    public Double getValue_level_of_control() {
        return value_level_of_control;
    }

    public Double getValue_visibility_overall() {
        return value_visibility_overall;
    }

    public Double getValue_privacy_score() {
        return value_privacy_score;
    }

    public Boolean getValue_is_inferred() {
        return value_is_inferred;
    }

    public List<Support> getSupports() {
        return supports;
    }

    public void setValue_confidence(Double value_confidence) {
        this.value_confidence = value_confidence;
    }

    public void setValue_visibility_label(Constants.VisibilityLabel value_visibility_label) {
        this.value_visibility_label = value_visibility_label;
    }

    public void setSensitivity(Double value_sensitivity) {
        this.value_sensitivity = value_sensitivity;
    }

    public void setValue_visibility_actual_audience(Integer value_visibility_actual_audience) {
        this.value_visibility_actual_audience = value_visibility_actual_audience;
    }

    public void setValue_level_of_control(Double value_level_of_control) {
        this.value_level_of_control = value_level_of_control;
    }

    public void setValue_visibility_overall(Double value_visibility_overall) {
        this.value_visibility_overall = value_visibility_overall;
    }

    public void setValue_privacy_score(Double value_privacy_score) {
        this.value_privacy_score = value_privacy_score;
    }

    public void setValue_is_inferred(Boolean value_is_inferred) {
        this.value_is_inferred = value_is_inferred;
    }

    public void computeScores(){
        Support mostImportantSupport=supports.get(0);
        for(Support support:supports)
            if(support.getSupport_confidence()>mostImportantSupport.getSupport_confidence())
                mostImportantSupport=support;
        computeConfidence(mostImportantSupport);
        computeVisibilityLabel(mostImportantSupport);
        computeLevelOfControl(mostImportantSupport);
        computeVisibilityOverall(mostImportantSupport);
        computePrivacyScore();
        //computeIsInferred(mostImportantSupport);
    }

    private void computeConfidence(Support support){
        value_confidence=support.getSupport_confidence();
    }

    private void computeVisibilityLabel(Support support){
        value_visibility_label=support.getSupport_visibility_label();
    }
    
    private void computeLevelOfControl(Support support){
        value_level_of_control=support.getSupport_level_of_control();
    }
    
    private void computeVisibilityOverall(Support support){
        value_visibility_overall=support.getSupport_visibility();
    }

    private void computePrivacyScore(){
        //May change the following to a sigmoid or a combination of only two of the variables
        if(value_confidence==null) System.out.println("Confidence is null");
        if(value_sensitivity==null) System.out.println("Sensitivity is null");
        if(value_visibility_overall==null) System.out.println("Visibility is null");
        Double result=value_sensitivity*value_visibility_overall*value_confidence;
        value_privacy_score=result;
    }

    /*
    private void computeIsInferred(Support support){
        value_is_inferred=support.getIsInferred();
    }    
    */
}
