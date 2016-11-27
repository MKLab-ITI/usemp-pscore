package usemp.certh.inference.preprepilot;

import com.restfb.types.Message;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.StatusMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import weka.core.tokenizers.WordTokenizer;

/**
 *
 * @author gpetkos
 * @author Eleftherios Spyromitros-Xioufis
 * 
 * This is a class that prepares the input for the pre-pilot classifier.
 * 
 */
public class Classification {
	/** the id of this user */
	private String id;
	/** a hashset with the likes of this user as objects  */
	private HashSet<Page> likes;
	private HashMap<String,Message> messages;
	private HashMap<String,Post> posts;
	private HashMap<String,StatusMessage> statuses;

	/** the ids of the likes of this user along with counts (all counts should be equal to 1 probably) */
	private HashMap<String, Integer> likesIdsCounts;
	/** the categories of the likes of this user along with counts */
	private HashMap<String, Integer> likesCatsCounts;
	/** the terms of the likes of this user along with counts */
	private HashMap<String, Integer> likesTermsCounts;

	/** the terms of the messages of this user along with counts */
	private HashMap<String, Integer> messagesTermsWithCounts;
	/** holds the concepts of this user along with their frequencies */
	private HashMap<String, Integer> conceptCounts;
	/** holds the concepts of this user along with the sum of their confidence scores */
	private HashMap<String, Double> conceptSums;

	private WordTokenizer wt;

        TreeMap<Integer, Double> feats_LDA_ml_20 = new TreeMap();
        TreeMap<Integer, Double> feats_LDA_ml_30 = new TreeMap();
        TreeMap<Integer, Double> feats_LDA_ml_50 = new TreeMap();
        TreeMap<Integer, Double> feats_LDA_ml_100 = new TreeMap();
        TreeMap<Integer, Double> feats_likes = new TreeMap();
        TreeMap<Integer, Double> feats_likesCats = new TreeMap();
        TreeMap<Integer, Double> feats_likesTerms = new TreeMap();
        TreeMap<Integer, Double> feats_messagesTerms = new TreeMap();
        TreeMap<Integer, Double> feats_concepts_bin = new TreeMap();
        TreeMap<Integer, Double> feats_concepts_freq = new TreeMap();
        TreeMap<Integer, Double> feats_concepts_conf = new TreeMap();
        
        
	public Classification(String id) {
		this.id = id.trim();
		likes = new HashSet<Page>();
                posts=new HashMap<String,Post>();
                statuses=new HashMap<String,StatusMessage>();
                
		likesIdsCounts = new HashMap<String, Integer>();
		likesCatsCounts = new HashMap<String, Integer>();
		likesTermsCounts = new HashMap<String, Integer>();
		messages = new HashMap<String,Message>();
		messagesTermsWithCounts = new HashMap<String, Integer>();
		conceptCounts = new HashMap<String, Integer>();
		conceptSums = new HashMap<String, Double>();
		wt = new WordTokenizer();
		wt.setDelimiters(Stopwords.delimiters);
	}

	public void addConcept(String concept, int frequency, double confidence) {
		// update the conceptCounts map
		Integer count = conceptCounts.get(concept);
		Double sum = conceptSums.get(concept);
		if (count == null) {
			conceptCounts.put(concept, frequency);
			conceptSums.put(concept, confidence);
		} else {
			conceptCounts.put(concept, count + frequency);
			conceptSums.put(concept, sum + confidence);
		}
	}
        
	public void addLike(Page like){
		// update like ids
		String likeId = like.getId();
		Integer likeCount = likesIdsCounts.get(likeId);
		if (likeCount != null) {
			likesIdsCounts.put(likeId, likeCount + 1);
		} else {
			likesIdsCounts.put(likeId, 1);
		}

		// update like cats
		String likeCat = like.getCategory();
		if (likeCat != null) {
			Integer likeCatCount = likesCatsCounts.get(likeCat);
			if (likeCatCount != null) {
				likesCatsCounts.put(likeCat, likeCatCount + 1);
			} else {
				likesCatsCounts.put(likeCat, 1);
			}
		}
		// update like terms
		HashMap<String, Integer> likeTerms = extractTerms(like);
		for (Entry<String, Integer> term : likeTerms.entrySet()) {
			Integer prevCount = likesTermsCounts.get(term.getKey());
			if (prevCount != null) {
				likesTermsCounts.put(term.getKey(), prevCount + term.getValue());
			} else {
				likesTermsCounts.put(term.getKey(), term.getValue());
			}
		}
		likes.add(like);
	}

	public void removeLike(Page like) {
		// update like ids
		String likeId = like.getId();
		Integer likeCount = likesIdsCounts.get(likeId);
		if (likeCount != null) {
                    Integer newLikeCount=likeCount-1;
                    if(newLikeCount==0){
                        likesIdsCounts.remove(likeId);
                    }
                    else{
                        likesIdsCounts.put(likeId, newLikeCount);
                    }
		}

		// update like cats
		String likeCat = like.getCategory();
		if (likeCat != null) {
			Integer likeCatCount = likesCatsCounts.get(likeCat);
			if (likeCatCount != null) {
                            Integer newLikeCatCount=likeCatCount-1;
                            if(newLikeCatCount==0){
				likesCatsCounts.remove(likeCat);
                            }
                            else{
				likesCatsCounts.put(likeCat, newLikeCatCount);
                            }
                        }
		}
                        
		// update like terms
//                like.rebuildTerms();
//		HashMap<String, Integer> likeTerms = like.getTerms();
		HashMap<String, Integer> likeTerms = extractTerms(like);
                for (Entry<String, Integer> term : likeTerms.entrySet()) {
                        Integer prevCount = likesTermsCounts.get(term.getKey());
                        Integer newCount=prevCount-1;
                        if(prevCount==0){
                                likesTermsCounts.remove(term.getKey());
                        }
                        else{
                                likesTermsCounts.put(term.getKey(), newCount);
                        }
                }
		likes.remove(like);
                make_null_likes();
                make_null_LDA();
	}
        

        public String getUserMLText(){
            String res="";
            for(Message message:messages.values())
                res=res+message.getMessage()+" ";
            for(Page like:likes){
                if(like!=null){
                    if(like.getDescription()!=null)
                        res=res+like.getDescription();
                    if(like.getDescription()!=null)
                        res=res+like.getDescription();
                }
            }
            return res;
        }


        
	public HashMap<String, Integer> getConceptCounts() {
		return conceptCounts;
	}

	public HashMap<String, Double> getConceptSums() {
		return conceptSums;
	}

	public HashSet<Page> getLikes() {
		return likes;
	}

	public HashMap<String, Integer> getLikesIdsCounts() {
		return likesIdsCounts;
	}

	public HashMap<String, Integer> getLikesCatsCounts() {
		return likesCatsCounts;
	}

	public HashMap<String, Integer> getLikesTermsCounts() {
		return likesTermsCounts;
	}

	public HashMap<String,Message> getMessages() {
		return messages;
	}

	public HashMap<String, Integer> getMessagesTermsWithCounts() {
		return messagesTermsWithCounts;
	}

        public String getId() {
            return id;
        }
        
        private HashMap<String, Integer> extractTerms(Page like){
            HashMap<String, Integer> terms=new HashMap<String, Integer>();

            WordTokenizer wt = new WordTokenizer();
            wt.setDelimiters(Stopwords.delimiters);

            if (like.getName() != null) {
                    wt.tokenize(like.getName().toLowerCase());
                    while (wt.hasMoreElements()) {
                            String word = wt.nextElement();
                            if (!Stopwords.isStopword(word)) {
                                    Integer count = terms.get(word);
                                    if (count == null) {
                                            terms.put(word, 1);
                                    } else {
                                            terms.put(word, count + 1);
                                    }
                            }
                    }
            }

            String about=like.getAbout();
            if (about!= null) {
                    wt.tokenize(about.toLowerCase());
                    while (wt.hasMoreElements()) {
                            String word = wt.nextElement();
                            if (!Stopwords.isStopword(word)) {
                                    Integer count = terms.get(word);
                                    if (count == null) {
                                            terms.put(word, 1);
                                    } else {
                                            terms.put(word, count + 1);
                                    }
                            }
                    }
            }

            String description=like.getDescription();
            if (description != null) {
                    wt.tokenize(description.toLowerCase());
                    while (wt.hasMoreElements()) {
                            String word = wt.nextElement();
                            if (!Stopwords.isStopword(word)) {
                                    Integer count = terms.get(word);
                                    if (count == null) {
                                            terms.put(word, 1);
                                    } else {
                                            terms.put(word, count + 1);
                                    }
                            }
                    }
            }
            return terms;
        }
        
	public void addConcept(String concept, double confidence) {
		// update the conceptCounts map
		Integer count = conceptCounts.get(concept);
		Double sum = conceptSums.get(concept);
		if (count == null) {
			conceptCounts.put(concept, 1);
			conceptSums.put(concept, confidence);
		} else {
			conceptCounts.put(concept, count + 1);
			conceptSums.put(concept, sum + confidence);
		}
                make_null_concepts();
	}

	/**
	 * Updates the conceptCounts and conceptSums map based on this concept and confidence score. *
	 * 
	 * @param concept
	 * @param confidence
	 */
	public void removeConcept(String concept, double confidence) {
		// update the conceptCounts map
		Integer count = conceptCounts.get(concept);
		Double sum = conceptSums.get(concept);
		if (count != null){
                        Integer newCount=count-1;
                        Double newSum=sum-confidence;
                        if(newCount==0){
                            conceptCounts.remove(concept);
                            conceptSums.remove(concept);
                        }
                        else{
                            conceptCounts.put(concept, newCount);
                            conceptSums.put(concept, newSum);
                        }
		}
                make_null_concepts();
	}
        
        /*
	public void addLike(Like like) throws Exception {
		// update like ids
		String likeId = like.getId();
		Integer likeCount = likesIdsCounts.get(likeId);
		if (likeCount != null) {
			likesIdsCounts.put(likeId, likeCount + 1);
		} else {
			likesIdsCounts.put(likeId, 1);
		}

		// update like cats
		String likeCat = like.getCategory();
		if (likeCat != null) {
			if (likeCat.equals("null")) {
				throw new Exception("Strange!");
			}
			Integer likeCatCount = likesCatsCounts.get(likeCat);
			if (likeCatCount != null) {
				likesCatsCounts.put(likeCat, likeCatCount + 1);
			} else {
				likesCatsCounts.put(likeCat, 1);
			}
		}
		// update like terms
		HashMap<String, Integer> likeTerms = like.getTerms();
		for (Entry<String, Integer> term : likeTerms.entrySet()) {
			Integer prevCount = likesTermsCounts.get(term.getKey());
			if (prevCount != null) {
				likesTermsCounts.put(term.getKey(), prevCount + term.getValue());
			} else {
				likesTermsCounts.put(term.getKey(), term.getValue());
			}
		}
		likes.add(like);
                make_null_likes();
                make_null_LDA();
	}

	public void removeLike(Like like) throws Exception {
		// update like ids
		String likeId = like.getId();
		Integer likeCount = likesIdsCounts.get(likeId);
		if (likeCount != null) {
                    Integer newLikeCount=likeCount-1;
                    if(newLikeCount==0){
                        likesIdsCounts.remove(likeId);
                    }
                    else{
                        likesIdsCounts.put(likeId, newLikeCount);
                    }
		}

		// update like cats
		String likeCat = like.getCategory();
		if (likeCat != null) {
			if (likeCat.equals("null")) {
				throw new Exception("Strange!");
			}
			Integer likeCatCount = likesCatsCounts.get(likeCat);
			if (likeCatCount != null) {
                            Integer newLikeCatCount=likeCatCount-1;
                            if(newLikeCatCount==0){
				likesCatsCounts.remove(likeCat);
                            }
                            else{
				likesCatsCounts.put(likeCat, newLikeCatCount);
                            }
                        }
		}
                        
		// update like terms
                like.rebuildTerms();
		HashMap<String, Integer> likeTerms = like.getTerms();
                for (Entry<String, Integer> term : likeTerms.entrySet()) {
                        Integer prevCount = likesTermsCounts.get(term.getKey());
                        Integer newCount=prevCount-1;
                        if(prevCount==0){
                                likesTermsCounts.remove(term.getKey());
                        }
                        else{
                                likesTermsCounts.put(term.getKey(), newCount);
                        }
                }
		likes.remove(like);
                make_null_likes();
                make_null_LDA();
	}
        */
        
	/**
	 * Adds the given message in the list of messages. Updates the messagesTermsWithCounts map based on this
	 * message.
	 * 
	 * @param message
	 */
	public void addMessage(Message messageO) {
//		messages.add(message);
		messages.put(messageO.getId(),messageO);
                String message=messageO.getMessage();
		// update the messagesTermsWithCounts map
		wt.tokenize(message.toLowerCase());
		while (wt.hasMoreElements()) {
			String word = wt.nextElement();
			if (!Stopwords.isStopword(word)) {
				Integer count = messagesTermsWithCounts.get(word);
				if (count == null)
					messagesTermsWithCounts.put(word, 1);
				else
					messagesTermsWithCounts.put(word, count + 1);
			}
		}
                make_null_messages();
	}

	public void removeMessage(String message) {
		messages.remove(message);
		// update the messagesTermsWithCounts map
		wt.tokenize(message.toLowerCase());
		while (wt.hasMoreElements()) {
			String word = wt.nextElement();
			if (!Stopwords.isStopword(word)) {
				Integer count = messagesTermsWithCounts.get(word);
                                Integer newCount= count-1;
                                if(newCount==0){
                                    messagesTermsWithCounts.remove(word);
                                }
                                else{
                                    messagesTermsWithCounts.put(word, newCount);
                                }
			}
		}
                make_null_messages();
	}

	/**
	 * Adds the given post in the list of posts. Updates the messagesTermsWithCounts map based on this
	 * post.
	 * 
	 * @param post
	 */
	public void addPost(Post postO) {
//		posts.add(post);
		posts.put(postO.getId(),postO);
                String post=postO.getMessage();
                if((post!=null)&&(post.trim().length()>0)){
                    // update the messagesTermsWithCounts map
                    wt.tokenize(post.toLowerCase());
                    while (wt.hasMoreElements()) {
                            String word = wt.nextElement();
                            if (!Stopwords.isStopword(word)) {
                                    Integer count = messagesTermsWithCounts.get(word);
                                    if (count == null)
                                            messagesTermsWithCounts.put(word, 1);
                                    else
                                            messagesTermsWithCounts.put(word, count + 1);
                            }
                    }
                    make_null_messages();
                }
	}

	public void removePost(Post postO) {
		posts.remove(postO);
		// update the messagesTermsWithCounts map
		wt.tokenize(postO.getMessage().toLowerCase());
		while (wt.hasMoreElements()) {
			String word = wt.nextElement();
			if (!Stopwords.isStopword(word)) {
				Integer count = messagesTermsWithCounts.get(word);
                                Integer newCount= count-1;
                                if(newCount==0){
                                    messagesTermsWithCounts.remove(word);
                                }
                                else{
                                    messagesTermsWithCounts.put(word, newCount);
                                }
			}
		}
                make_null_messages();
	}
        
	/**
	 * Adds the given status in the list of statuses. Updates the messagesTermsWithCounts map based on this
	 * post.
	 * 
	 * @param status
	 */
	public void addStatus(StatusMessage statusO) {
//		posts.add(post);
		statuses.put(statusO.getId(),statusO);
                String status=statusO.getMessage();
		// update the messagesTermsWithCounts map
                if((status!=null)&&(status.trim().length()>0)){
                    wt.tokenize(status.toLowerCase());
                    while (wt.hasMoreElements()) {
                            String word = wt.nextElement();
                            if (!Stopwords.isStopword(word)) {
                                    Integer count = messagesTermsWithCounts.get(word);
                                    if (count == null)
                                            messagesTermsWithCounts.put(word, 1);
                                    else
                                            messagesTermsWithCounts.put(word, count + 1);
                            }
                    }
                    make_null_messages();
                }
	}

	public void removeStatus(StatusMessage statusO) {
		statuses.remove(statusO);
		// update the messagesTermsWithCounts map
		wt.tokenize(statusO.getMessage().toLowerCase());
		while (wt.hasMoreElements()) {
			String word = wt.nextElement();
			if (!Stopwords.isStopword(word)) {
				Integer count = messagesTermsWithCounts.get(word);
                                Integer newCount= count-1;
                                if(newCount==0){
                                    messagesTermsWithCounts.remove(word);
                                }
                                else{
                                    messagesTermsWithCounts.put(word, newCount);
                                }
			}
		}
                make_null_messages();
	}
        
    public void make_null_LDA(){
        feats_LDA_ml_20 = null;
        feats_LDA_ml_30 = null;
        feats_LDA_ml_50 = null;
        feats_LDA_ml_100 = null;
    }
    
    
    public void make_null_likes(){
        feats_likes = null;
        feats_likesCats = null;
        feats_likesTerms = null;
    }

    public void make_null_messages(){
        feats_messagesTerms = null;
    }
    
    public void make_null_concepts(){
        feats_concepts_bin = null;
        feats_concepts_freq = null;
        feats_concepts_conf = null;
    }
    
}
