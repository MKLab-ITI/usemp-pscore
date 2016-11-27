package usemp.certh.scoring;

import com.restfb.types.Message;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.management.Query;
import usemp.certh.userDataAccess.UserDataAccess;

/**
 *
 * @author gpetkos
 * 
 * This class represents the association between attributes and social network data.
 * It essentially the result of an inference mechanism.
 * 
 */
public class Support {
    private Constants.InferenceMechanism support_inference_mechanism; // Provided during initialization
    private List<String> support_data_pointer_ids; //Provided during initialization. 
    private Double support_confidence; //Provided during initialization
    private Double support_level_of_control; //Computed based on data pointers
    private Double support_visibility; //Computed based on data pointers
    private Constants.VisibilityLabel support_visibility_label; //Computed based on data pointers
    private String support_description_en;
    private String support_description_sw;
    private String support_description_du;
    private boolean support_is_main;
    private static Random rnd=new Random();
    
    
    public Support(Support support) {
        support_inference_mechanism = support.support_inference_mechanism;
        support_data_pointer_ids=new ArrayList<String>();
        for(String dpid:support.support_data_pointer_ids){
            support_data_pointer_ids.add(dpid);
        }
        support_confidence = support.support_confidence;
        support_level_of_control=support.support_level_of_control;
        support_visibility=support.support_visibility;
        support_visibility_label=support.support_visibility_label;
        support_description_en= support.support_description_en;
        support_description_du= support.support_description_du;
        support_description_sw=support.support_description_sw;
        support_is_main=support.support_is_main;
    }    
    
    public Support(Constants.InferenceMechanism support_inference_mechanism, List<String> support_data_pointer_ids, Double support_confidence, String user_id, String support_description_en,String support_description_du,String support_description_sw,UserDataAccess user_data) {
        this.support_inference_mechanism = support_inference_mechanism;
        this.support_data_pointer_ids = support_data_pointer_ids;
        this.support_confidence = support_confidence;
        this.support_description_en=support_description_en;
        this.support_description_du=support_description_du;
        this.support_description_sw=support_description_sw;

        //
        Set<String> messages=new HashSet<String>();
        Set<String> images=new HashSet<String>();
        Set<String> likes=new HashSet<String>();
        
        //Need to update next lines when the data are available
        for(String id:support_data_pointer_ids){
            String[] parts=id.split(" ");
            String iid=parts[1].trim();
            if(id.startsWith("IMAGE"))
                images.add(iid);
            if(id.startsWith("POST"))
                messages.add(iid);
            if(id.startsWith("LIKE"))
                likes.add(iid);
        }

        //Fist compute level of control. This is computed as the minimum over the "control"
        //for the three different types of data in the support. 
        //For likes, the control is 1, for the rest, it's:
        //||posted by user|| / ||all items in support||
        Double support_level_of_control_likes=1.0;
        Double support_level_of_control_images=0.0;
        double count=0;
        for(String imageId:images){
            if(imageId.equals("*")){
                HashSet<Photo> imagesO=user_data.getAllPhotos();
                count=imagesO.size();
                for(Photo photo:imagesO)
                    if(photo.getFrom().getId().equals(user_id))
                        support_level_of_control_images=support_level_of_control_images+1.0;
            }
            else{
                /*
                Photo photoO=getImage(imageId,mongoOperations);
                    if((photoO!=null)&&(photoO.getFrom().getId().equals(user_id))){
                        support_level_of_control_images=support_level_of_control_images+1.0;
                        count=count+1.0;
                    }
                */
                support_level_of_control_images=support_level_of_control_images+1.0;
                count=count+1.0;
            }
        }
        if(count!=0.0)
            support_level_of_control_images=support_level_of_control_images/count;
        else
            support_level_of_control_images=1.0;

        Double support_level_of_control_posts=0.0;
        count=0;
        for(String postId:messages){
            if(postId.equals("*")){
                HashSet<Post> posts=user_data.getAllPosts();
                count=posts.size();
                for(Post post:posts)
                    if(post.getFrom().getId().equals(user_id))
                        support_level_of_control_posts=support_level_of_control_posts+1.0;
            }
            else{
                Post post=user_data.getPost(postId);
                if(post.getFrom().getId().equals(user_id)){
                        support_level_of_control_posts=support_level_of_control_posts+1.0;
                        count=count+1.0;
                    }
            }
        }
        if(count!=0.0)
            support_level_of_control_posts=support_level_of_control_posts/count;
        else
            support_level_of_control_posts=1.0;

        support_level_of_control=support_level_of_control_likes;
        if(support_level_of_control_images<support_level_of_control)
            support_level_of_control=support_level_of_control_images;
        if(support_level_of_control_posts<support_level_of_control)
            support_level_of_control=support_level_of_control_posts;
        
        
        //Then compute the two visibility scores. 
        //This is computed as the minimum over the visibility of the different
        //types of data.
        //For likes visibility is 1 (all your friends see your likes)
        //For images and posts we use the details provided by fb's API.
        Long noOfFriends=new Long(user_data.getFriendsCount());
        if(noOfFriends==null) noOfFriends=1000l;
        Double support_visibility_likes=1.0;
        Constants.VisibilityLabel support_visibility_label_likes=Constants.VisibilityLabel.EVERYONE;

        Double support_visibility_images=1.0;
        Constants.VisibilityLabel support_visibility_label_images=Constants.VisibilityLabel.EVERYONE;
        for(String imageId:images){
            if(imageId.equals("*")){
                //TO DO: It's better to run through all the images and compute it.
                //Another option would be to get default sharing prefs.
                support_visibility_label_images=Constants.VisibilityLabel.FRIENDS;
                support_visibility_images=1.0;
            }
            else{
                //TODO: fix next 2 lines
//                String priv=imagePrivacy(imageId,user_data);
                String priv="FRIENDS 0 0";
                
                String[] privParts=priv.split(" ");
                String label=privParts[0];
                Integer countAllow=Integer.parseInt(privParts[1]);
                Integer countDeny=Integer.parseInt(privParts[2]);
                if(label.equals("ALL_FRIENDS")) label="FRIENDS";
                Constants.VisibilityLabel support_visibility_label_images_tmp=Constants.VisibilityLabel.valueOf(label);
                Double support_visibility_images_tmp=1.0;
                if(label.equals("CUSTOM")){
                    if(countAllow!=0){
                        support_visibility_images_tmp=((double) countAllow)/((double) noOfFriends);
                    }
                    if(countDeny!=0){
                        support_visibility_images_tmp=(noOfFriends- ((double) countAllow))/((double) noOfFriends);
                    }
                }
                if(label.equals("SELF")){
                    support_visibility_images_tmp=0.0;
                }
                if(label.equals("EVERYONE")) support_visibility_images_tmp=1.0;
                if(label.equals("FRIENDS")) support_visibility_images_tmp=1.0;
                if(label.equals("FRIENDS_OF_FRIENDS")) support_visibility_images_tmp=1.0;
                
                
                if(support_visibility_images>support_visibility_images_tmp) 
                    support_visibility_images=support_visibility_images_tmp;
                if(support_visibility_label_images_tmp.compareTo(support_visibility_label_images)>0)
                    support_visibility_label_images=support_visibility_label_images_tmp;
            }
        }
        
        
        Double support_visibility_posts=1.0;
        Constants.VisibilityLabel support_visibility_label_posts=Constants.VisibilityLabel.EVERYONE;
        for(String postId:messages){
            if(postId.equals("*")){
                //TO DO: It's better to run through all the posts and compute it.
                //Another option would be to get default sharing pref.
                support_visibility_label_posts=Constants.VisibilityLabel.FRIENDS;
                support_visibility_posts=1.0;
            }
            else{
                Post post=user_data.getPost(postId);
                String priv=postPrivacy(postId,user_data);
                String[] privParts=priv.split(" ");
                String label=privParts[0];
                Integer countAllow=Integer.parseInt(privParts[1]);
                if(countAllow==null) countAllow = noOfFriends.intValue();
                Integer countDeny=Integer.parseInt(privParts[2]);
                if(label.equals("ALL_FRIENDS")) label="FRIENDS";
                Constants.VisibilityLabel support_visibility_label_posts_tmp=Constants.VisibilityLabel.valueOf(label);
                Double support_visibility_posts_tmp=1.0;
                if(label.equals("CUSTOM")){
                    if(countAllow!=0){
                        support_visibility_posts_tmp=((double) countAllow)/((double) noOfFriends);
                    }
                    if(countDeny!=0){
                        support_visibility_posts_tmp=(noOfFriends- ((double) countAllow))/((double) noOfFriends);
                    }
                }
                if(label.equals("SELF")){
                    support_visibility_posts_tmp=0.0;
                }
                if(label.equals("EVERYONE")) support_visibility_posts_tmp=1.0;
                if(label.equals("FRIENDS")) support_visibility_posts_tmp=1.0;
                if(label.equals("FRIENDS_OF_FRIENDS")) support_visibility_posts_tmp=1.0;
                
                
                if(support_visibility_posts>support_visibility_posts_tmp) 
                    support_visibility_posts=support_visibility_posts_tmp;
                if(support_visibility_label_posts_tmp.compareTo(support_visibility_label_posts)>0)
                    support_visibility_label_posts=support_visibility_label_posts_tmp;
            }
        }
        
        support_visibility=support_visibility_likes;
        if(support_visibility_images<support_visibility)
            support_visibility=support_visibility_images;
        if(support_visibility_posts<support_visibility)
            support_visibility=support_visibility_posts;

        support_visibility_label=support_visibility_label_likes;
        if(support_visibility_label_images.compareTo(support_visibility_label)>0)
            support_visibility_label=support_visibility_label_images;
        if(support_visibility_label_posts.compareTo(support_visibility_label)>0)
            support_visibility_label=support_visibility_label_posts;
        
        
        //2) support_visibility
        //support_visibility=0.25+0.5*rnd.nextDouble();
        //support_visibility=1.0;
        //3) support_visibility_label
        support_visibility_label=Constants.VisibilityLabel.FRIENDS;
        
    }

    public Support() {
        //
        Set<Post> posts=new HashSet<Post>();
        Set<Photo> photos=new HashSet<Photo>();
    }

    public void setSupport_is_main(boolean support_is_main) {
        this.support_is_main = support_is_main;
    }

    public void setSupport_description_en(String support_description_en) {
        this.support_description_en = support_description_en;
    }

    public void setSupport_description_sw(String support_description_sw) {
        this.support_description_sw = support_description_sw;
    }

    public void setSupport_description_du(String support_description_du) {
        this.support_description_du = support_description_du;
    }

    public String getSupport_description_en() {
        return support_description_en;
    }

    public String getSupport_description_du() {
        return support_description_du;
    }

    public String getSupport_description_sw() {
        return support_description_sw;
    }
    
    public boolean isSupport_is_main() {
        return support_is_main;
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

    /*
    public static HashMap<Photo,PrivacyInfo> getUserImagesAndPrivacy(String user_id,FileDataAccess ){
        Query q = new Query();
        q.addCriteria(Criteria.where("from._id").is(user_id));
        List<Photo> photos = mongoOperation.find(q, Photo.class);
        
        HashMap<Photo,PrivacyInfo> result=new HashMap<Photo,PrivacyInfo>();
        for(Photo photo:photos){
            PrivacyInfo pi=mongoOperation.findById(photo.getId(),PrivacyInfo.class);
            if(pi!=null) result.put(photo, pi);
        }
        return result;
    }
*/
    
    public static String postPrivacy(String post_id,UserDataAccess user_data){
        Post post=user_data.getPost(post_id);
        if(post==null)
            return "FRIENDS 0 0";
        else{
            String[] allow=post.getPrivacy().getAllow().split(",");
            String[] deny=post.getPrivacy().getDeny().split(",");
            return post.getPrivacy().getValue()+" "+allow.length+" "+deny.length;
        }
    }

  /*  
    public static String imagePrivacy(String imageId,MongoOperations mongoOperation){
        PrivacyInfo pi=mongoOperation.findById(imageId, PrivacyInfo.class);
        if(pi==null)
            return "FRIENDS 0 0";
        else{
            String desc=pi.getFinalPrivacyDescription();
            String general="FRIENDS";
            Integer allow=0;
            Integer deny=0;
            if(desc.contains("friends")) general="FRIENDS";
            if(desc.contains("self")) general="SELF";
            if(desc.contains("everyone")) general="EVERYONE";
            return general+" "+allow+" "+deny;
        }
    }
    */
}
