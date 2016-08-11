package usemp.certh.userDataAccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.types.Album;
import com.restfb.types.Page;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import com.restfb.types.User;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import usemp.certh.scoring.ScoringUser;
import usemp.visual.images.Concept;
import usemp.visual.images.Image;
import usemp.visual.images.VisualDetection;

/**
 *
 * @author gpetkos
 * 
 * This is an implementation of the interface UserDataAccess.
 * It retrieves the data of a user from the set of files saved from 
 * the FacebookPersonalDataFetcher class.
 * 
 */
public class UserDataAccessFromFile implements UserDataAccess {
    
    String directory=null;
    HashSet<Page> likes=null;
    HashSet<Post> posts=null;
    HashSet<StatusMessage> statuses=null;
    HashSet<Photo> photos=null;
    HashSet<Album> albums=null;
    HashSet<User> friends=null;
    ScoringUser scoringUser=null;
    User user=null;
    HashMap<String,Integer> conceptsFrequency=null;
    HashMap<String,Double> conceptsTotalConfidence=null;
    HashMap<String,Double> conceptsConfidenceMax=null;
    HashMap<String,String> conceptsConfidenceMaxImage=null;
    
    
    public UserDataAccessFromFile(String _directory) {
        directory=_directory;
        
    }
    
    public HashSet<Page> getAllLikes(){
        if(likes==null){
            likes=new HashSet<Page>();
            String filename=directory+"myLikes.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    Page page = jsonMapper.toJavaObject(line, Page.class);   
                    likes.add(page);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return likes;
    }

    public Page getLike(String id){
        HashSet<Page> pagesI=getAllLikes();
        for(Page page:pagesI)
            if(page.getId().equals(id))
                return page;
        return null;
    }
    
    public HashSet<Post> getAllPosts(){
        if(posts==null){
            posts=new HashSet<Post>();
            String filename=directory+"myPosts.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    Post post = jsonMapper.toJavaObject(line, Post.class);   
                    posts.add(post);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return posts;
    }

    public Post getPost(String id){
        HashSet<Post> postsI=getAllPosts();
        for(Post post:postsI)
            if(post.getId().equals(id))
                return post;
        return null;
    }
    
    public HashSet<StatusMessage> getAllStatuses(){
        if(statuses==null){
            statuses=new HashSet<StatusMessage>();
            String filename=directory+"myStatuses.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    StatusMessage status = jsonMapper.toJavaObject(line, StatusMessage.class);   
                    statuses.add(status);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return statuses;
    }

    public StatusMessage getStatus(String id){
        HashSet<StatusMessage> statusesI=getAllStatuses();
        for(StatusMessage status:statusesI)
            if(status.getId().equals(id))
                return status;
        return null;
    }
    
    public HashSet<Photo> getAllPhotos(){
        if(photos==null){
            photos=new HashSet<Photo>();
            String filename=directory+"myPhotos.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    Photo photo = jsonMapper.toJavaObject(line, Photo.class);   
                    photos.add(photo);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return photos;
    }
    
    public Photo getPhoto(String id){
        HashSet<Photo> photosI=getAllPhotos();
        for(Photo photo:photosI)
            if(photo.getId().equals(id))
                return photo;
        return null;
    }
    
    
    public HashSet<Album> getAllAlbums(){
        if(albums==null){
            albums=new HashSet<Album>();
            String filename=directory+"myAlbums.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    Album album = jsonMapper.toJavaObject(line, Album.class);   
                    albums.add(album);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return albums;
    }
    
    public Album getAlbum(String id){
        HashSet<Album> albumsI=getAllAlbums();
        for(Album album:albumsI)
            if(album.getId().equals(id))
                return album;
        return null;
    }

    public HashSet<User> getAllFriends(){
        if(friends==null){
            friends=new HashSet<User>();
            String filename=directory+"myFriends.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line="";
                while((line=br.readLine())!=null){
                    User user = jsonMapper.toJavaObject(line, User.class);   
                    friends.add(user);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return friends;
    }
    
    public int getFriendsCount(){
        HashSet<User> friendsO=getAllFriends();
        return friendsO.size();
    }

    public ScoringUser getScoringUser(){
        if(scoringUser==null){
            String filename=directory+"myScores.json";
            File scoresFile=new File(filename);
            if(scoresFile.exists()){
                try{
                    BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(filename), "UTF8"));
                    String line=br.readLine();
                    scoringUser=new ScoringUser();
                    ObjectMapper mapper = new ObjectMapper();
                    scoringUser=mapper.readValue(line, ScoringUser.class);
                    br.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return scoringUser;
    }
    
    public void saveScoringUser(){
        if(scoringUser!=null){
            String filename=directory+"myScores.json";
            try{
                BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
                ObjectMapper mapper = new ObjectMapper();
                String scoresString=scoringUser.toJSonString();
                bw.append(scoresString);
                bw.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public User getUser(){
        if(user==null){
            String filename=directory+"myDetails.json";
            JsonMapper jsonMapper = new DefaultJsonMapper();
            try{
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF8"));
                String line=br.readLine();
                user = jsonMapper.toJavaObject(line, User.class);   
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return user;
    }

    public HashMap<String,Integer> getVisualConceptsFrequency(){
        if(conceptsFrequency==null)
            loadImagesData();
        return conceptsFrequency;
    }

    public HashMap<String,Double> getVisualConceptsTotalConfidence(){
        if(conceptsTotalConfidence==null)
            loadImagesData();
        return conceptsTotalConfidence;
    }

    private void loadImagesData(){
        conceptsFrequency=new HashMap<String, Integer>();
        conceptsTotalConfidence= new HashMap<String,Double>();
        conceptsConfidenceMax=new HashMap<String,Double>();
        conceptsConfidenceMaxImage=new HashMap<String,String>();
        String filename=directory+"concepts.xml";
        File imagesFile = new File(filename);
        if (imagesFile.exists()) {
            try{
                InputStream imageconcepts = new BufferedInputStream(new FileInputStream(filename));

                JAXBContext jcImages = JAXBContext.newInstance("usemp.visual.images");
                Unmarshaller umImages = jcImages.createUnmarshaller();
                VisualDetection vd = (VisualDetection) umImages.unmarshal(imageconcepts);

                imageconcepts.close();

                List<Image> images = vd.getImage();

                System.out.println("Num images:" + images.size());
                for (Image i : images) {
                    List<Concept> concepts = i.getConcept();
                    for (Concept ct : concepts) {

                        Integer frequency=conceptsFrequency.get(ct.getName());
                        if(frequency==null)
                            conceptsFrequency.put(ct.getName(), 1);
                        else
                            conceptsFrequency.put(ct.getName(), frequency+1);

                        Double totalConf=conceptsTotalConfidence.get(ct.getName());
                        if(totalConf==null)
                            conceptsTotalConfidence.put(ct.getName(), ct.getConfidence().doubleValue());
                        else
                            conceptsTotalConfidence.put(ct.getName(), totalConf+ct.getConfidence().doubleValue());
                        
                        Double confidenceMax=conceptsConfidenceMax.get(ct.getName());
                        Double conceptConfidence=ct.getConfidence().doubleValue();
                        if((confidenceMax==null)||(confidenceMax<conceptConfidence)){
                            conceptsConfidenceMax.put(ct.getName(), conceptConfidence);
                            conceptsConfidenceMaxImage.put(ct.getName(), i.getId());
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    
    public HashMap<String,Double> getVisualConceptsMaxConfidence(){
        if(conceptsConfidenceMax==null)
            loadImagesData();
        return conceptsConfidenceMax;
    }

    public HashMap<String,String> getVisualConceptsMaxConfidenceImage(){
        if(conceptsConfidenceMaxImage==null)
            loadImagesData();
        return conceptsConfidenceMaxImage;
        
    }
    
}
