/*
 * Copyright 2016 gpetkos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package usemp.certh.scoring;

/**
 *
 * @author gpetkos
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
    String pointer;

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

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }
    
}
