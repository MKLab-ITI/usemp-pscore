package usemp.certh.scoring;

/**
 *
 * @author gpetkos
 * 
 * This is not currently used in the demo but will be in the next version 
 * that will be uploaded soon
 * 
 */
public class ControlSuggestion {
    String id;
    String dimension;
    String attribute;
    String value;
    Double confidence;
    Double score;
    String description_en;
    String description_du;
    String description_sw;

    public ControlSuggestion(String id, String dimension, String attribute, String value, Double confidence, Double score, String description_en, String description_du, String description_sw) {
        this.id = id;
        this.dimension = dimension;
        this.attribute = attribute;
        this.value = value;
        this.confidence = confidence;
        this.score = score;
        this.description_en = description_en;
        this.description_du = description_du;
        this.description_sw = description_sw;
    }

    public String getId() {
        return id;
    }

    public String getDimension() {
        return dimension;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public Double getConfidence() {
        return confidence;
    }

    public Double getScore() {
        return score;
    }

    public String getDescription_en() {
        return description_en;
    }

    public String getDescription_du() {
        return description_du;
    }

    public String getDescription_sw() {
        return description_sw;
    }

    
    
    public void setId(String id) {
        this.id = id;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public void setDescription_du(String description_du) {
        this.description_du = description_du;
    }

    public void setDescription_sw(String description_sw) {
        this.description_sw = description_sw;
    }

    
    
}
