package tech.mabase.www.mycroftspeakskill;

import android.content.ContentValues;

/**
 * Created by Chris on 11/9/2017.
 *
 * This class exists to parse information from text files
 * and to format it for installing in Mycroft
 */

public class AssetInstallClass {

    //This might technically be done by AssetInstallClass
    public static ContentValues[] getVocab() {
        /*
        for (int i=0; i < vocabWords; i++) {
            for(int j = 0; j < 3; j++) {
                vocab[i][j].put("SKILL", "Speech");
                vocab[i][j].put("VARNAME", variable);
                vocab[i][j].put("word", word);
            }
        }
        */
        return null;
    }

    public static ContentValues[] getIntents(){
        /*
        for (int i = 0; i < numOfIntents; i++){
            loadIntents();
        }
        return intents
        */
        return null;
    }
}
