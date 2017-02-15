package usemp.certh.inference.urlclassifier;

import com.restfb.types.Message;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.Query;

import usemp.certh.scoring.Constants;
import usemp.certh.scoring.ScoringUser;
import usemp.certh.userDataAccess.UserDataAccess;

/**
 *
 * @author gpetkos
 * 
 * This class is the implementation of an inference module that examines the 
 * URLs that a user has posted and associates them to specific privacy attributes.
 * For instance, posting a URL in imdb can be associated to the 
 * attribute "movies series" (under the "Hobbies" dimension)
 * 
 */
public class URLClassifier {

    private HashMap<String,HashSet<String>> mapping;

    private Double defaultConfidence=0.9;


    public URLClassifier() {
        BufferedReader in;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            in = new BufferedReader(
                    new InputStreamReader(classLoader.getResourceAsStream("urls_to_privacy_values.txt"), "UTF8"));

            String line;
            mapping=new HashMap<String,HashSet<String>>();
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                HashSet<String> categories=mapping.get(parts[0]);
                if(categories==null)
                    categories=new HashSet<String>();
                categories.add(parts[1]);
                mapping.put(parts[0], categories);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Mapping from URLS to privacy values loaded!");

    }

    private ArrayList<String> extractURLs(String text) {
        ArrayList<String> links = new ArrayList<String>();
        if(text==null) return links;

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")){
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }    
    
    
    private HashMap<String,String> getUserURLs(UserDataAccess user_data){
        HashMap<String,String> urlsToContent=new HashMap<String,String>();
        
        HashSet<Post> posts = user_data.getAllPosts();
        for(Post post:posts){
            String text=post.getMessage();
            ArrayList<String> urls=extractURLs(text);
            for(String url:urls)
                urlsToContent.put(url, post.getId());
        }

        HashSet<StatusMessage> statuses=user_data.getAllStatuses();
        for(StatusMessage status:statuses){
            String text=status.getMessage();
            ArrayList<String> urls=extractURLs(text);
            for(String url:urls)
                urlsToContent.put(url, status.getId());
        }

        return urlsToContent;
    }

    
    public HashMap<String,String> getUserURLsText(UserDataAccess user_data){
        HashMap<String,String> urlsToContent=new HashMap<String,String>();
        
        HashSet<Post> posts=user_data.getAllPosts();
        for(Post post:posts){
            String text=post.getMessage();
            ArrayList<String> urls=extractURLs(text);
            for(String url:urls)
                urlsToContent.put(post.getMessage(), url);
        }
        
        HashSet<StatusMessage> statuses = user_data.getAllStatuses();
        for(StatusMessage status:statuses){
            String text=status.getMessage();
            ArrayList<String> urls=extractURLs(text);
            for(String url:urls)
                urlsToContent.put(status.getMessage(), url);
        }

        return urlsToContent;
    }
    
    
    public void classify(UserDataAccess user_data) {
        ScoringUser scoringUser = user_data.getScoringUser();

        HashMap<String,String> urls=getUserURLs(user_data);

        HashMap<String,HashMap<String,HashSet<String>>> counts=new HashMap<String,HashMap<String,HashSet<String>>>();
        
        for(String url:urls.keySet()){
            String urlS=url;
            if(!urlS.contains("://")) urlS="http://"+urlS;
            if(!urlS.endsWith("/")) urlS=urlS+"/";
            HashSet<String> privacyValues=mapping.get(urlS);
            if(privacyValues==null){
                try{
                    URL urlO=new URL(urlS);
                    if(urlO!=null){
                        urlS=urlO.getProtocol()+"://"+urlO.getHost()+"/";
                        privacyValues=mapping.get(urlS);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(privacyValues!=null){
                for(String privacyValue:privacyValues){
                    String[] parts=privacyValue.split("\\$");
                    String dimension=parts[0];
                    String attribute=parts[1];
                    String value=parts[2];
                    String dimAtt=dimension+"$"+attribute;
                    HashMap<String,HashSet<String>> count=counts.get(dimAtt);
                    if(count==null){
                        count=new HashMap<String,HashSet<String>>();
                        counts.put(dimAtt,count);
                    }
                    HashSet<String> lks=count.get(value);
                    if(lks==null){
                        lks=new HashSet<String>();
                        count.put(value,lks);
                    }
                    lks.add(url);
                }
            }
        }
 
        
        for(Entry<String,HashMap<String,HashSet<String>>> entry:counts.entrySet()){
            String[] parts=entry.getKey().split("\\$");
            String dimension=parts[0];
            String attribute=parts[1];
            HashMap<String,Double> probs=new HashMap<String,Double>();
            Double totalCount=0.0;
            Boolean uniqueVal=false;
            for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                totalCount=totalCount+ent.getValue().size();
            }
            if(entry.getValue().entrySet().size()==1) uniqueVal=true;           
            
            if(uniqueVal){
                for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                    List<String> pointersToData=new ArrayList<String>();
//                    String description="Because you have posted the following URLs: ";
                    String description_en="This value has been associated with "+Constants.confidence_en(defaultConfidence)+" confidence to the following URLs that you have posted:  ";
                    String description_du="Deze resultaten werden geassocieerd met de websites die je deelt op Facebook met een "+Constants.confidence_du(defaultConfidence)+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(defaultConfidence)+" konfidens på följande URL:er som du har delat: ";
                    for(String url:ent.getValue()){
                        pointersToData.add("POST "+urls.get(url));
                        description_en=description_en+url+" , ";
                        description_du=description_du+url+" , ";
                        description_sw=description_sw+url+" , ";
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, defaultConfidence,
                            usemp.certh.scoring.Constants.InferenceMechanism.URLS_MAPPING,description_en,description_du, description_sw, user_data);
                }
            }
            else{
                for(Entry<String,HashSet<String>> ent:entry.getValue().entrySet()){
                    List<String> pointersToData=new ArrayList<String>();
//                    String description="Because you have posted the following URLs: ";
                    String description_en="This value has been associated with "+Constants.confidence_en(ent.getValue().size()/totalCount)+" confidence to the following URLs that you have posted:  ";
                    String description_du="Deze resultaten werden geassocieerd met de websites die je deelt op Facebook met een "+Constants.confidence_du(ent.getValue().size()/totalCount)+" zekerheid: ";
                    String description_sw="Dessa resultat baseras med "+Constants.confidence_sw(ent.getValue().size()/totalCount)+" konfidens på följande URL:er som du har delat: ";
                    for(String url:ent.getValue()){
                        pointersToData.add("POST "+urls.get(url));
//                        description=description+url+" , ";
                        description_en=description_en+url+" , ";
                        description_du=description_du+url+" , ";
                        description_sw=description_sw+url+" , ";
                    }
                    description_en=description_en.trim();
                    description_en=description_en.substring(0,description_en.length()-1);
                    description_du=description_du.trim();
                    description_du=description_du.substring(0,description_du.length()-1);
                    description_sw=description_sw.trim();
                    description_sw=description_sw.substring(0,description_sw.length()-1);
                    scoringUser.addSupport(dimension, attribute, ent.getKey(), pointersToData, ent.getValue().size()/totalCount,
                            usemp.certh.scoring.Constants.InferenceMechanism.URLS_MAPPING,description_en, description_du,description_sw,user_data);
                }
            }
        }
    }
    
    public HashMap<String, HashSet<String>> getMapping() {
        return mapping;
    }

    public void setMapping(HashMap<String, HashSet<String>> mapping) {
        this.mapping = mapping;
    }

}
