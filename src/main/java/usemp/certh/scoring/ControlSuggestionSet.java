package usemp.certh.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author gpetkos
 * 
 * This is also not used in this version, but will be in the next.
 * 
 */
public class ControlSuggestionSet {
    ArrayList<ControlSuggestion> likes;
    ArrayList<ControlSuggestion> posts;
    String user_id;

    public ControlSuggestionSet(String _user_id) {
        likes=new ArrayList<ControlSuggestion>();
        posts=new ArrayList<ControlSuggestion>();
        user_id=_user_id;
    }

    public ArrayList<ControlSuggestion> getLikes() {
        return likes;
    }

    public ArrayList<ControlSuggestion> getPosts() {
        return posts;
    }

    public void addControlSuggestionLike(ControlSuggestion cs){
        likes.add(cs);
    }
    
    public void addControlSuggestionPost(ControlSuggestion cs){
        posts.add(cs);
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
    
    public void orderAll(){
        orderPosts();
        orderLikes();
    }
    
    
}
