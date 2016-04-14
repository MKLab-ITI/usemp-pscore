/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author gpetkos
 */
public class DisclosureScoringFramework {
    
    // TODO. Second option for linking to mongo: use the mongooperations class
    // rather that the mongo driver. I guess we will either do this or just 
    // change the connection settings.

    private MongoDatabase db;

    public DisclosureScoringFramework() {
        MongoClient mongoClient = new MongoClient(Constants.mongoHost);
        db = mongoClient.getDatabase(Constants.mongoDatabase);    
    }
    
    
    //Gets user from the mongo. If the user does not exist, it is created.
    public ScoringUser getUser(String id){
        FindIterable<Document> iterable = db.getCollection(Constants.mongoCollection).find(
            new Document("user_id", id));
        
        ObjectMapper mapper = new ObjectMapper();

        List<Document> documents=new ArrayList<Document>();
        ScoringUser user=null;
        Document document=iterable.first();
        
        if(document!=null){
            document.remove("_id");
            String jsonInString=document.toJson();
            try {
                    // Convert JSON string to Object
                    user = mapper.readValue(jsonInString, ScoringUser.class);
                    //Pretty print
                    //String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff1);
                    //System.out.println(prettyStaff1);
            } catch (JsonGenerationException e) {
                    e.printStackTrace();
            } catch (JsonMappingException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }        
        }
        else
            user=new ScoringUser(id);
        return user;
    }
    
    public void saveUser(ScoringUser user){
        FindIterable<Document> iterable = db.getCollection(Constants.mongoCollection).find(
            new Document("user_id", user.getUser_id()));
        Document document=iterable.first();
        
        String userString=user.toJSonString();
        Document doc = Document.parse(userString);
        if(document==null){
            db.getCollection(Constants.mongoCollection).insertOne(doc);
        }
        else{
            db.getCollection(Constants.mongoCollection).findOneAndReplace(
                new Document("user_id", user.getUser_id()), doc);
        }
    }
    
    public String getUserScoresJson(String id){

        ScoringUser user=getUser(id);
        if(user!=null)
            return user.toJSonString();
        else
            return null;
    }
    
}
