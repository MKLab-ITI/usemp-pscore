package usemp.certh.userDataAccess;

import com.restfb.types.Album;
import com.restfb.types.Page;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import com.restfb.types.User;
import java.util.HashMap;
import java.util.HashSet;
import usemp.certh.scoring.ScoringUser;

/**
 *
 * @author gpetkos
 * 
 * This is an interface that defines the data access operations for a specific user.
 * 
 */
public interface UserDataAccess {
    
    public HashSet<Page> getAllLikes();

    public Page getLike(String likeId);
    
    public HashSet<Post> getAllPosts();

    public Post getPost(String postId);
    
    public HashSet<StatusMessage> getAllStatuses();
    
    public StatusMessage getStatus(String statusId);
    
    public HashSet<Photo> getAllPhotos();
    
    public Photo getPhoto(String photoId);
    
    public HashSet<Album> getAllAlbums();
    
    public Album getAlbum(String albumId);

    public HashSet<User> getAllFriends();
    
    public int getFriendsCount();

    public ScoringUser getScoringUser();
    
    public void saveScoringUser();

    public User getUser();
    
    public HashMap<String,Integer> getVisualConceptsFrequency();

    public HashMap<String,Double> getVisualConceptsTotalConfidence();

    public HashMap<String,Double> getVisualConceptsMaxConfidence();

    public HashMap<String,String> getVisualConceptsMaxConfidenceImage();
    
}
