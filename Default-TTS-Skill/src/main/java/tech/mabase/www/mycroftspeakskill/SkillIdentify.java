package tech.mabase.www.mycroftspeakskill;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class SkillIdentify extends IntentService{

    //This is the string used for a debugger ID
    public static final String TAG = "Mycroft Speak Skill";
    //This is the string representing the content provider Authority
    public static final String AUTHORITY = "www.mabase.tech.mycroft";
    //These are the strings for the assets
    public static final String VOCAB = "file:///android_asset/vocab";
    public static final String REGEX = "file:///android_asset/regex";
    public static final String INTENT = "file:///android_asset/intent";

    //This is received to identify the package as an Adapt skill, and to trigger the install process
    public static final String ACTION_SKILL_IDENTIFY = "android.intent.action.ADAPT_SKILL_IDENTIFY";
    //This is an action sent to register the content provider authority with Adapt
    public static final String ACTION_SKILL_INSTALL = "android.intent.action.ADAPT_SKILL_IDENTIFY";
    //This is Adapt Content Provider
    public static final String PROVIDER_NAME = "www.mabase.tech.mycroft";
    //These are the two URI
    public static final Uri ENTITY_URI = Uri.parse("content://"+AUTHORITY+"/entity");
    public static final Uri INTENT_URI = Uri.parse("content://"+AUTHORITY+"/intent");


    public SkillIdentify() {
        super("SkillIdentify");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SKILL_IDENTIFY.equals(action)) {
                handleSkillIdentify();
            }
        }
    }

    /*
    Perhaps this should be skillInstall. It will be identified simply by querying
    the packageManager. The ony time this service will be run is when a skill
    is installed...
     */
    private void handleSkillIdentify() {
        Log.i(TAG,"Skill identified");

        //Install to content provider Adapt, by using content resolver
        ContentValues[] vocab =  AssetInstallClass.getVocab();
        ContentValues[] intents = AssetInstallClass.getIntents();

        //This physically inserts the skills info into Adapt
        //It takes the vocab, and loads it into the entities table
        //If regex were to exist, it would do the same with them
        getContentResolver().bulkInsert(ENTITY_URI, vocab);
        //For intents, it takes the intents, breaks them down and loads them into the Intent table
        getContentResolver().bulkInsert(INTENT_URI, intents);

    }
}