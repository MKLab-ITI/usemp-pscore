/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import com.restfb.types.Photo;
import com.restfb.types.Post;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import usemp.certh.inference.preprepilot.Like;

/**
 *
 * @author gpetkos
 */
public class Support {
    private Constants.InferenceMechanism support_inference_mechanism; // Provided during initialization
    private List<String> support_data_pointer_ids; //Provided during initialization. 
    private Double support_confidence; //Provided during initialization
    private Double support_level_of_control; //Computed based on data pointers
    private Double support_visibility; //Computed based on data pointers
    private Constants.VisibilityLabel support_visibility_label; //Computed based on data pointers
    private static Random rnd=new Random();
    
//    private Boolean isInferred;
//    private String support_data_pointer_type;

    //TODO. In the following we will compute:
    // a) support_level_of_control
    // b) support_visibility
    // c) support_visibility_label
    // To compute these we need:
    // a) A function that returns all the images of the user
    // b) A function that returns all the posts of the user
    // c) A function that returns all the likes of the user
    // d) For each image / post we need its author and its sharing (privacy) settings.
    //    So, for the functions above, the objects in the sets returned should have
    //    these two pieces of information in them. Additionally, we may want to query
    //    for a specific image or post using its id and the returned object should have
    //    these two pieces of information. Regarind the images, as far as I remember, 
    //    we can have the privacy settings for the album in which each photo belongs. 
    public Support(Constants.InferenceMechanism support_inference_mechanism, List<String> support_data_pointer_ids, Double support_confidence, String user_id) {
        this.support_inference_mechanism = support_inference_mechanism;
        this.support_data_pointer_ids = support_data_pointer_ids;
        this.support_confidence = support_confidence;
        
        //
        Set<Post> posts=new HashSet<Post>();
        Set<Photo> photos=new HashSet<Photo>();
        Set<Like> likes=new HashSet<Like>();
        
        //Need to update next lines when the data are available
        for(String id:support_data_pointer_ids){
            if(id.equals("ALL_IMAGES")){
                //TODO. Get all photos of the user (update next line)
                //photos.addAll(Get all phots from backend)
            } else
            if(id.equals("ALL_POSTS")){
                //TODO. Get all posts of the user (update next line)
                //posts.addAll(Get all phots from backend)
            } else
            if(id.equals("ALL_LIKES")){
                //TODO. Get all likes of the user (update next line)
                //likes.addAll(Get all likes from backend)
            } 
            //TODO. Complete the following:
            //else
            //if(id corresponds to image)
            //  photos.add(getImageWithId(id));
            //else
            //if(id corresponds to post)
            //  posts.add(getPostWithId(id));
            //if(id corresponds to like)
            //  likes.add(getLikeWithId(id));
            
        }
        
        //Then, we will use the data pointers to compute the rest of the fields:
        //The following are just dummy computations for the moment, 
        //once we have the actual data I will will do the actual computation
        //1) support_level_of_control
//        support_level_of_control=0.25+0.5*rnd.nextDouble();
        support_level_of_control=1.0;
        //2) support_visibility
        support_visibility=1.0;
        //3) support_visibility_label
        support_visibility_label=Constants.VisibilityLabel.FRIENDS;
        
    }

    public Support() {
        //
        Set<Post> posts=new HashSet<Post>();
        Set<Photo> photos=new HashSet<Photo>();
    }
    
    
    
    public Constants.InferenceMechanism getSupport_inference_mechanism() {
        return support_inference_mechanism;
    }

    public List<String> getSupport_data_pointer_ids() {
        return support_data_pointer_ids;
    }

    public Double getSupport_level_of_control() {
        return support_level_of_control;
    }

    /*
    public String getSupport_data_pointer_type() {
        return support_data_pointer_type;
    }
*/
    
    public Double getSupport_confidence() {
        return support_confidence;
    }

    public void setSupport_inference_mechanism(Constants.InferenceMechanism support_inference_mechanism) {
        this.support_inference_mechanism = support_inference_mechanism;
    }

    /*
    public void setSupport_data_pointer_id(List<String> support_data_pointer_ids) {
        this.support_data_pointer_ids = support_data_pointer_ids;
    }
    

    public void setSupport_level_of_control(Double support_level_of_control) {
        this.support_level_of_control = support_level_of_control;
    }

    public void setSupport_data_pointer_type(String support_data_pointer_type) {
        this.support_data_pointer_type = support_data_pointer_type;
    }

    public void setSupport_confidence(Double support_confidence) {
        this.support_confidence = support_confidence;
    }
    
    */
    

    public Double getSupport_visibility() {
        return support_visibility;
    }

    public Constants.VisibilityLabel getSupport_visibility_label() {
        return support_visibility_label;
    }

    /*
    public Boolean getIsInferred() {
        return isInferred;
    }
*/
    
    
}
