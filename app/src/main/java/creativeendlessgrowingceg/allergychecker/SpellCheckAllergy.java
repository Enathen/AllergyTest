package creativeendlessgrowingceg.allergychecker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Enathen on 2017-10-19.
 */

public class SpellCheckAllergy {
    private static final String TAG = "SpellCheckAllergy";
    HashMap<String,LangString> hashMap = new HashMap<>();
    HashMap<String,char[]> alphabets = new HashMap<>();

    public SpellCheckAllergy(){
        alphabets.put("en","abcdefghijklmnopqrstuvwxyz".toCharArray()); //English
        alphabets.put("sv","abcdefghijklmnopqrstuvwxyzåäö".toCharArray()); //Swedish
        alphabets.put("nb","abcdefghijklmnopqrstuvwxyzåæø".toCharArray()); //Norwegian
        alphabets.put("da","abcdefghijklmnopqrstuvwxyzåæø".toCharArray()); //Danish
        alphabets.put("fi","abcdefghijklmnopqrstuvwxyzåäö".toCharArray()); //Finish
        alphabets.put("de","abcdefghijklmnopqrstuvwxyzäöüß".toCharArray()); //German
        alphabets.put("es","abcdefghijklmnñopqrstuvwxyz".toCharArray()); //Spanish

    }
    public void convertString(){

        for (String key : hashMap.keySet()) {
            if(hashMap.get(key).on){
                if(hashMap.get(key).allPossibleDerivationsOfAllergen == null){
                    Log.d(TAG,"Key: "+key + " open "+ hashMap.get(key).on);
                    LangString languageString = hashMap.get(key);
                    HashSet<String> arrayList = new HashSet<>();
                    languageString.addallPossibleDerivationsOfAllergen(AlgorithmString(key,arrayList,hashMap.get(key).language));
                    hashMap.put(key,languageString);
                }
            }
        }

        for (String key : hashMap.keySet()) {

            //Log.d(TAG,"Key: " + key);
            for (String string : hashMap.get(key).allPossibleDerivationsOfAllergen) {
                //Log.d(TAG,string);
            }
        }
        Log.d(TAG,"Total walue"+String.valueOf(hashMap.keySet().size()));
    }

    private HashSet<String> AlgorithmString(String s, HashSet<String> arrayListNew,String language) {
        s = s.replaceAll("[^\\p{L}\\p{Nd}\\s]+", "");
        s= s.toLowerCase();
        StringBuilder string;
        for (int i = 0; i < s.length()+1; i++) {
            if(i!=s.length()) {
                string = new StringBuilder(s);
                string = string.deleteCharAt(i);
                if(string.length()>2){
                    arrayListNew.add(String.valueOf(string));
                }
                if(i!=s.length()-1) {
                    string = new StringBuilder(s);
                    char replace = string.charAt(i);
                    string.setCharAt(i,string.charAt(i+1));
                    string.setCharAt(i+1,replace);
                    arrayListNew.add(String.valueOf(string));
                }
            }
            for (char c : alphabets.get(language)) {
                string = new StringBuilder(s);
                arrayListNew.add(String.valueOf(string.replace(i,i, String.valueOf(c))));
                //Log.d(TAG,"TEST Replace Char:"+String.valueOf(string));
                if(i!=s.length()){
                    string = new StringBuilder(s);
                    string.setCharAt(i, c);
                    arrayListNew.add(String.valueOf(string));
                    //Log.d(TAG,"TestSetCharAt"+String.valueOf(string));
                }
            }
        }
        arrayListNew.add(s);
        arrayListNew.remove("salt");
        return arrayListNew;
    }
    public HashSet<String> permuteString(String language, String string){
        HashSet<String> arrayListNew = new HashSet<>();
        List<String> list = null;
        if(string.contains(",")){
             list = Arrays.asList(string.split(","));

        }
        if (list != null){
            for (String s : list) {
                arrayListNew = AlgorithmString(s,arrayListNew,language);

            }
        }else {
            arrayListNew = AlgorithmString(string,arrayListNew,language);
        }

        //convertString();
        return arrayListNew;
    }
    public HashMap<String,LangString> permuteStringi(Activity context,HashMap<Integer,LanguageString> hashMap){
        for (LanguageString languageString : hashMap.values()) {
            HashMap<String,HashSet<String>> allPossibleWords = languageString.allPossibleWords;
            for (String strings : allPossibleWords.keySet()) {
                this.hashMap.put(getStringByLocal(context,languageString.id,strings),new LangString(strings,true,languageString.id,allPossibleWords.get(strings)));
            }
        }
        for (String s : this.hashMap.keySet()) {
            Log.d(TAG, s);
        }
        convertString();
        return this.hashMap;
    }
    public String removeSuffixes(String string){

        return string;
    }
    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Activity context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id).toLowerCase();
    }


}
