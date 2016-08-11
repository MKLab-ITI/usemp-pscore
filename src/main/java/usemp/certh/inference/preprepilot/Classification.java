package usemp.certh.inference.preprepilot;

import com.restfb.types.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

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

	/** the ids of the likes of this user along with counts (all counts should be equal to 1 probably) */
	private HashMap<String, Integer> likesIdsCounts;
	/** the categories of the likes of this user along with counts */
	private HashMap<String, Integer> likesCatsCounts;
	/** the terms of the likes of this user along with counts */
	private HashMap<String, Integer> likesTermsCounts;

	/** the messages of this user as an ArrayList of Strings (probably not needed) */
	private ArrayList<String> messages;
	/** the terms of the messages of this user along with counts */
	private HashMap<String, Integer> messagesTermsWithCounts;
	/** holds the concepts of this user along with their frequencies */
	private HashMap<String, Integer> conceptCounts;
	/** holds the concepts of this user along with the sum of their confidence scores */
	private HashMap<String, Double> conceptSums;

	private WordTokenizer wt;

	public Classification(String id) {
		this.id = id.trim();
		likes = new HashSet<Page>();
		likesIdsCounts = new HashMap<String, Integer>();
		likesCatsCounts = new HashMap<String, Integer>();
		likesTermsCounts = new HashMap<String, Integer>();
		messages = new ArrayList<String>();
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
        
	public void addLike(Page like) throws Exception {
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

	/**
	 * Adds the given message in the list of messages. Updates the messagesTermsWithCounts map based on this
	 * message.
	 * 
	 * @param message
	 */
	public void addMessage(String message) {
		messages.add(message);
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
	}

        public String getUserMLText(){
            String res="";
            for(String message:messages)
                res=res+message+" ";
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

	public ArrayList<String> getMessages() {
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
        
        
}
