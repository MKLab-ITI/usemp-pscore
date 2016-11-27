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
import java.util.HashMap;

/**
 *
 * @author gpetkos
 */
public class ControlSuggestionSetExtended {
    HashMap<String,ArrayList<ControlSuggestion>> likes;
    HashMap<String,ArrayList<ControlSuggestion>> posts;
    HashMap<String,ArrayList<ControlSuggestion>> images;
    String user_id;

    public ControlSuggestionSetExtended(String _user_id) {
        likes=new HashMap<String,ArrayList<ControlSuggestion>>();
       
        String dimension="Overall";
        ArrayList<ControlSuggestion> tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Demographics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Hobbies";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Employment";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Relationships";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Religion";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Sexuality";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Politics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Health";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        dimension="Psychology";
        tmp_arr=new ArrayList<ControlSuggestion>();
        likes.put(dimension, tmp_arr);
        
        posts=new HashMap<String,ArrayList<ControlSuggestion>>();
        dimension="Overall";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Demographics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Hobbies";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Employment";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Relationships";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Religion";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Sexuality";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Politics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Health";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
        dimension="Psychology";
        tmp_arr=new ArrayList<ControlSuggestion>();
        posts.put(dimension, tmp_arr);
  
        images=new HashMap<String,ArrayList<ControlSuggestion>>();
        dimension="Overall";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Demographics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Hobbies";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Employment";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Relationships";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Religion";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Sexuality";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Politics";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Health";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        dimension="Psychology";
        tmp_arr=new ArrayList<ControlSuggestion>();
        images.put(dimension, tmp_arr);
        
        user_id=_user_id;
    }

    public HashMap<String,ArrayList<ControlSuggestion>> getLikes() {
        return likes;
    }

    public HashMap<String,ArrayList<ControlSuggestion>> getPosts() {
        return posts;
    }

    public HashMap<String,ArrayList<ControlSuggestion>> getImages() {
        return images;
    }

    
    public void addControlSuggestionLike(ControlSuggestion cs){
        ArrayList<ControlSuggestion> list=likes.get(cs.getDimension());
        if(list==null){
            list=new ArrayList<ControlSuggestion>();
            likes.put(cs.getDimension(), list);
        }
        list.add(cs);
    }
    
    public void addControlSuggestionPost(ControlSuggestion cs){
        ArrayList<ControlSuggestion> list=posts.get(cs.getDimension());
        if(list==null){
            list=new ArrayList<ControlSuggestion>();
            posts.put(cs.getDimension(), list);
        }
        list.add(cs);
    }

    public void addControlSuggestionImage(ControlSuggestion cs){
        ArrayList<ControlSuggestion> list=images.get(cs.getDimension());
        if(list==null){
            list=new ArrayList<ControlSuggestion>();
            images.put(cs.getDimension(), list);
        }
        list.add(cs);
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void orderLikes(){
        for(ArrayList<ControlSuggestion> list:likes.values()){
            Collections.sort(list, new Comparator<ControlSuggestion>(){
                public int compare(ControlSuggestion o1, ControlSuggestion o2){
                    if(o1.score == o2.score)
                        return 0;
                    return o1.score < o2.score ? 1 : -1;
                }
           });
        }
    }

    public void orderPosts(){
        for(ArrayList<ControlSuggestion> list:posts.values()){
            Collections.sort(list, new Comparator<ControlSuggestion>(){
                public int compare(ControlSuggestion o1, ControlSuggestion o2){
                    if(o1.score == o2.score)
                        return 0;
                    return o1.score < o2.score ? 1 : -1;
                }
           });
        }
    }

    public void orderImages(){
        for(ArrayList<ControlSuggestion> list:images.values()){
            Collections.sort(list, new Comparator<ControlSuggestion>(){
                public int compare(ControlSuggestion o1, ControlSuggestion o2){
                    if(o1.score == o2.score)
                        return 0;
                    return o1.score < o2.score ? 1 : -1;
                }
           });
        }
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
