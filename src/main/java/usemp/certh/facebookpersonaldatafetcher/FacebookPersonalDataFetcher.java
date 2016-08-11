package usemp.certh.facebookpersonaldatafetcher;

import com.github.axet.wget.WGet;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.json.JsonObject;
import com.restfb.types.Album;
import com.restfb.types.Page;
import com.restfb.types.Photo;
import com.restfb.types.User;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;
import usemp.certh.scoring.ScoringUser;

/**
 *
 * @author gpetkos
 * 
 * This class fetches the data of a user from Facebook and saves them in files
 * A valid access token and a target directory are required.
 * 
 */
public class FacebookPersonalDataFetcher {

    public static void fetchData(String accessToken,String targetDir){
        JsonMapper jsonMapper = new DefaultJsonMapper();
        
        File targetDirFile=new File(targetDir);
        if(!targetDirFile.exists()) targetDirFile.mkdir();
        
        String photosSubdir=targetDir+"photos/";
        File photosSubDirFile=new File(photosSubdir);
        if(!photosSubDirFile.exists()) photosSubDirFile.mkdir();
        
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);

        JsonObject userMe=facebookClient.fetchObject("me", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myDetails.json"), "UTF8"));
            bw.append(userMe.toString());
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
        Connection<JsonObject> myPosts = facebookClient.fetchConnection("me/posts", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myPosts.json"), "UTF8"));
            for (List<JsonObject> myPostConnectionPage : myPosts)
                for (JsonObject post : myPostConnectionPage){
                    bw.append(post.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Connection<JsonObject> myStatuses = facebookClient.fetchConnection("me/statuses", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myStatuses.json"), "UTF8"));
            for (List<JsonObject> myStatusConnectionPage : myStatuses)
                for (JsonObject status : myStatusConnectionPage){
                    bw.append(status.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Connection<JsonObject> myLikes = facebookClient.fetchConnection("me/likes", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myLikes.json"), "UTF8"));
            for (List<JsonObject> myLikesConnectionPage : myLikes)
                for (JsonObject like : myLikesConnectionPage){
                    Page page = jsonMapper.toJavaObject(like.toString(), Page.class);   
                    JsonObject likeJ=facebookClient.fetchObject(page.getId(), JsonObject.class);
                    bw.append(likeJ.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        Connection<JsonObject> myFriends = facebookClient.fetchConnection("me/friends", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myFriends.json"), "UTF8"));
            for (List<JsonObject> myFriendsConnectionPage : myFriends)
                for (JsonObject friend : myFriendsConnectionPage){
                    bw.append(friend.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
        Connection<JsonObject> myAlbums = facebookClient.fetchConnection("me/albums", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myAlbums.json"), "UTF8"));
            for (List<JsonObject> myAlbumsConnectionPage : myAlbums)
                for (JsonObject album : myAlbumsConnectionPage){
                    bw.append(album.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        Connection<JsonObject> myPhotos = facebookClient.fetchConnection("me/photos", JsonObject.class);
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetDir+"myPhotos.json"), "UTF8"));
            for (List<JsonObject> myPhotosConnectionPage : myPhotos)
                for (JsonObject photo : myPhotosConnectionPage){
                    bw.append(photo.toString());
                    bw.newLine();
                    Photo photoO = jsonMapper.toJavaObject(photo.toString(), Photo.class);   
                    File tmpF=new File(photosSubdir+photoO.getId());
                    WGet w = new WGet(new URL(photoO.getSource().trim()),photosSubDirFile);
                    w.download();
                }
            for (List<JsonObject> myAlbumsConnectionPage : myAlbums)
                for (JsonObject albumJ : myAlbumsConnectionPage){
                    Album album = jsonMapper.toJavaObject(albumJ.toString(), Album.class);       
                    Connection<JsonObject> albumPhotos = facebookClient.fetchConnection(album.getId()+"/photos", JsonObject.class);
                    for (List<JsonObject> myAlbumsPhotosConnectionPage : albumPhotos)
                        for (JsonObject photo : myAlbumsPhotosConnectionPage){
                            bw.append(photo.toString());
                            bw.newLine();
                            Photo photoO = jsonMapper.toJavaObject(photo.toString(), Photo.class);       
                            File tmpF=new File(photosSubdir+photoO.getId());
                            WGet w = new WGet(new URL(photoO.getSource().trim()),photosSubDirFile);
                            w.download();
                        }
                }
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        
        User me = jsonMapper.toJavaObject(userMe.toString(), User.class);   
        String userId=me.getId();
        ScoringUser scoringUser=new ScoringUser(userId);
        String filename=targetDir+"myScores.json";
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
            String scoresString=scoringUser.toJSonString();
            bw.append(scoresString);
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
