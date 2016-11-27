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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author gpetkos
 */
public class ControlSuggestionSet {
    ArrayList<ControlSuggestion> likes;
    ArrayList<ControlSuggestion> posts;
    ArrayList<ControlSuggestion> images;
    String user_id;

    public ControlSuggestionSet(String _user_id) {
        likes=new ArrayList<ControlSuggestion>();
        posts=new ArrayList<ControlSuggestion>();
        images=new ArrayList<ControlSuggestion>();
        user_id=_user_id;
    }

    public ArrayList<ControlSuggestion> getLikes() {
        return likes;
    }

    public ArrayList<ControlSuggestion> getPosts() {
        return posts;
    }

    public ArrayList<ControlSuggestion> getImages() {
        return images;
    }

    
    public void addControlSuggestionLike(ControlSuggestion cs){
        likes.add(cs);
    }
    
    public void addControlSuggestionPost(ControlSuggestion cs){
        posts.add(cs);
    }

    public void addControlSuggestionImage(ControlSuggestion cs){
        images.add(cs);
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void orderLikes(){
        Collections.sort(likes, new Comparator<ControlSuggestion>(){
            public int compare(ControlSuggestion o1, ControlSuggestion o2){
                if(o1.score == o2.score)
                    return 0;
                return o1.score < o2.score ? 1 : -1;
            }
       });
    }

    public void orderPosts(){
        Collections.sort(posts, new Comparator<ControlSuggestion>(){
            public int compare(ControlSuggestion o1, ControlSuggestion o2){
                if(o1.score == o2.score)
                    return 0;
                return o1.score < o2.score ? 1 : -1;
            }
       });
    }

    public void orderImages(){
        Collections.sort(images, new Comparator<ControlSuggestion>(){
            public int compare(ControlSuggestion o1, ControlSuggestion o2){
                if(o1.score == o2.score)
                    return 0;
                return o1.score < o2.score ? 1 : -1;
            }
       });
    }
    
    public void orderAll(){
        orderPosts();
        orderLikes();
        orderImages();
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
    
    
    
}
