package usemp.certh.scoring;

import java.util.HashMap;

/**
 *
 * @author gpetkos
 * 
 * A utility class for producing some translations of used text
 * 
 */
public class Translator {
    private static HashMap<String,String> wordsSw=new HashMap<String,String>();
    private static HashMap<String,String> wordsDu=new HashMap<String,String>();
    static{
        wordsSw.put("Demographics","Demografi");
        wordsSw.put("Hobbies","Hobbies");
        wordsSw.put("Employment","Sysselsättning");
        wordsSw.put("Relationships","Relationer");
        wordsSw.put("Religion","Religion");
        wordsSw.put("Sexuality","Sexuell läggning");
        wordsSw.put("Politics","Politiska åsikter");
        wordsSw.put("Health","Hälsa");
        wordsSw.put("Psychology","Personlighetsdrag");
       
        wordsDu.put("Demographics","Demografische gegevens");
        wordsDu.put("Hobbies","Hobby's");
        wordsDu.put("Employment","Tewerkstelling");
        wordsDu.put("Relationships","Relaties");
        wordsDu.put("Religion","Religie");
        wordsDu.put("Sexuality","Seksualiteit");
        wordsDu.put("Politics","Politieke voorkeuren");
        wordsDu.put("Health","Gezondheid");
        wordsDu.put("Psychology","Psychologische kenmerken");
      
        wordsSw.put("degree", "grad");
        wordsSw.put("nationality", "nationalitet");
        wordsSw.put("gender", "kön");
        wordsSw.put("animals", "djur");
        wordsSw.put("camping", "camping");
        wordsSw.put("dancing", "dans");
        wordsSw.put("gardening", "trädgårdsarbete");
        wordsSw.put("theatre", "teater");
        wordsSw.put("hiking", "vandring");
        wordsSw.put("sports", "idrott");
        wordsSw.put("music", "musik");
        wordsSw.put("reading", "läsande");
        wordsSw.put("shopping", "shopping");
        wordsSw.put("travelling", "att resa");
        wordsSw.put("series movies", "TV-serier och filmer");
        wordsSw.put("employment", "sysselsättning");
        wordsSw.put("income", "inkomst");
        wordsSw.put("living situation", "boendesituation");
        wordsSw.put("relationship status", "relationsstatus");
        wordsSw.put("religious practice", "religionsutövning");
        wordsSw.put("practice", "utövning");
        wordsSw.put("religious stance", "religiös övertygelse");
        wordsSw.put("belief", "tro");
        wordsSw.put("sexual orientation", "sexuell läggning");
        wordsSw.put("orientation", "läggning");
        wordsSw.put("political ideology", "politisk ideologi");
        wordsSw.put("ideology", "ideologi");
        wordsSw.put("alcohol", "alkohol");
        wordsSw.put("BMI class", "BMI");
        wordsSw.put("cannabis", "cannabis");
        wordsSw.put("coffee", "kaffe");
        wordsSw.put("exercising", "motion");
        wordsSw.put("health status", "hälsostatus");
        wordsSw.put("smoking", "rökning");
        wordsSw.put("agreeableness", "vänlighet");
        wordsSw.put("conscientiousness", "samvetsgrannhet");
        wordsSw.put("extraversion", "extraversion");
        wordsSw.put("neuroticism", "neuroticism");
        wordsSw.put("openness", "öppenhet");

        wordsDu.put("degree", "graad");
        wordsDu.put("nationality", "nationaliteit");
        wordsDu.put("gender", "gender");
        wordsDu.put("animals", "dieren");
        wordsDu.put("camping", "camping");
        wordsDu.put("dancing", "dancing");
        wordsDu.put("gardening", "tuinieren");
        wordsDu.put("theatre", "theater");
        wordsDu.put("hiking", "wandelen");
        wordsDu.put("sports", "sport");
        wordsDu.put("music", "muziek");
        wordsDu.put("reading", "lezen");
        wordsDu.put("shopping", "shoppen");
        wordsDu.put("travelling", "reizen");
        wordsDu.put("series movies", "series en films");
        wordsDu.put("employment", "tewerkstelling");
        wordsDu.put("income", "inkomen");
        wordsDu.put("living situation", "leefsituatie");
        wordsDu.put("relationship status", "relatiestatus");
        wordsDu.put("religious practice", "praktiserend gelovig");
        wordsDu.put("practice", "");  ////////////////////////////////////
        wordsDu.put("religious stance", "religieuze overtuiging");
        wordsDu.put("belief", "");  /////////////////////////////////////
        wordsDu.put("sexual orientation", "seksuele orientatie");
        wordsDu.put("orientation", "orientatie");
        wordsDu.put("political ideology", "politieke ideologie");
        wordsDu.put("ideology", "ideologie");
        wordsDu.put("alcohol", "alcohol");
        wordsDu.put("BMI class", "BMI-waarde");
        wordsDu.put("cannabis", "cannabis");
        wordsDu.put("coffee", "koffie");
        wordsDu.put("exercising", "beweging");
        wordsDu.put("health status", "gezondheid");
        wordsDu.put("smoking", "roken");
        wordsDu.put("agreeableness", "altruisme");
        wordsDu.put("conscientiousness", "nauwgezetheid");
        wordsDu.put("extraversion", "extraversie");
        wordsDu.put("neuroticism", "neuroticisme");
        wordsDu.put("openness", "openheid");
    }
    
    public static String translateDu(String original){
        String result=wordsDu.get(original);
        if(result==null) result=original;
        return result;
    }
    
    public static String translateSw(String original){
        String result=wordsSw.get(original);
        if(result==null) result=original;
        return result;
    }
    
}
