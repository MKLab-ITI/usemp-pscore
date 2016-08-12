package usemp.certh.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gpetkos
 * 
 * This class represents an attribute in the disclosure scoring framework
 * 
 */
public class Attribute {
    private Boolean normalized;
    private String attribute_name;
    private Double attribute_level_of_control;
    private Constants.VisibilityLabel attribute_visibility_label;
    private Double attribute_sensitivity;
    private Integer attribute_visibility_actual_audience;
    private Double attribute_visibility_overall;
    private Double attribute_privacy_score;
//    private Map<String,Value> values;
    private List<Value> values;

    public Attribute(Boolean normalized, String attribute_name) {
        this.normalized = normalized;
        this.attribute_name = attribute_name;
//        values=new HashMap<String,Value>();
        values=new ArrayList<Value>();
    }

    public Attribute() {
//        values=new HashMap<String,Value>();
        values=new ArrayList<Value>();
    }
    
    
    public Value addValue(String value_name){
  //      Value value=values.get(value_name);
        Value value=null;
        for(Value valueT:values)
            if(valueT.getValue_name().equals(value_name)){
                value=valueT;
                break;
            }
        
        if(value==null){
            value=new Value(value_name);
//            values.put(value_name, value);
            values.add(value);
        }
        return value;
    }
    
    public Value getValue(String value_name){
//        return values.get(value_name);
        Value value=null;
        for(Value valueT:values)
            if(valueT.getValue_name().equals(value_name)){
                value=valueT;
                break;
            }
        return value;
    }

    public Boolean getNormalized() {
        return normalized;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public Double getAttribute_level_of_control() {
        return attribute_level_of_control;
    }

    public Constants.VisibilityLabel getAttribute_visibility_label() {
        return attribute_visibility_label;
    }

    public Double getAttribute_sensitivity() {
        return attribute_sensitivity;
    }

    public Integer getAttribute_visibility_actual_audience() {
        return attribute_visibility_actual_audience;
    }

    public Double getAttribute_visibility_overall() {
        return attribute_visibility_overall;
    }

    public Double getAttribute_privacy_score() {
        return attribute_privacy_score;
    }

    public void setNormalized(Boolean normalized) {
        this.normalized = normalized;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public void setAttribute_level_of_control(Double attribute_level_of_control) {
        this.attribute_level_of_control = attribute_level_of_control;
    }

    public void setAttribute_visibility_label(Constants.VisibilityLabel attribute_visibility_label) {
        this.attribute_visibility_label = attribute_visibility_label;
    }

    public void setAttribute_sensitivity(Double attribute_sensitivity) {
        this.attribute_sensitivity = attribute_sensitivity;
    }

    public void setAttribute_visibility_actual_audience(Integer attribute_visibility_actual_audience) {
        this.attribute_visibility_actual_audience = attribute_visibility_actual_audience;
    }

    public void setAttribute_visibility_overall(Double attribute_visibility_overall) {
        this.attribute_visibility_overall = attribute_visibility_overall;
    }

    public void setAttribute_privacy_score(Double attribute_privacy_score) {
        this.attribute_privacy_score = attribute_privacy_score;
    }

    /*
    public Map<String, Value> getValues() {
        return values;
    }
    */
    public List<Value> getValues() {
        return values;
    }

    public void setSensitivityCascade(Double sensitivity){
        attribute_sensitivity=sensitivity;
        for(Value value:values) value.setSensitivity(sensitivity);
    }

	/*
    public void computeScores(){
        for(Value value:values)
            value.computeScores();
        aggregatePrivacyScores(values);
        aggregateVisibilityOverall(values);
        aggregateLevelOfControl(values);
    //    aggregateLevelOfControl(values.values());
        aggregateVisibilityLabel(values);
        }
*/
    public void computeScores(){
        Set<Value> toDelete=new HashSet<Value>();
        for(Value value:values){
            if(value.getSupports().size()>0)
                value.computeScores();
            else
                toDelete.add(value);
           
        }
        if(toDelete.size()>0)
            System.out.println("Deleting values: "+toDelete.size());
        values.removeAll(toDelete); 
        aggregatePrivacyScores(values);
        aggregateVisibilityOverall(values);
        aggregateLevelOfControl(values);
    //    aggregateLevelOfControl(values.values());
        aggregateVisibilityLabel(values);
    }
    
    private void aggregatePrivacyScores(Collection<Value> values){
        Double result=0.0;
        for(Value value:values)
            if(value.getValue_privacy_score()>result)
                result=value.getValue_privacy_score();
        attribute_privacy_score=result;
    }
    
    /*
    private void aggregatePrivacyScores(Collection<Value> values){
        Double result=0.0;
        for(Value value:values)
            result=result+value.getValue_privacy_score();
        result=result/values.size();
        attribute_privacy_score=result;
    }
    */
    
    private void aggregateVisibilityOverall(Collection<Value> values){
        Double result=0.0;
        for(Value value:values)
            result=result+value.getValue_visibility_overall();
        result=result/values.size();
        this.attribute_visibility_overall=result;
    }

    private void aggregateLevelOfControl(Collection<Value> values){
        Double result=Double.MAX_VALUE;
        for(Value value:values)
            if(value.getValue_level_of_control()<result)
                result=value.getValue_level_of_control();
        this.attribute_level_of_control=result;
    }
    
    private void aggregateVisibilityLabel(Collection<Value> values){
        Constants.VisibilityLabel result=Constants.VisibilityLabel.SELF;
        for(Value value:values)
            if(value.getValue_visibility_label().compareTo(result)<0)
                result=value.getValue_visibility_label();
        this.attribute_visibility_label=result;
    }
    
    
}
