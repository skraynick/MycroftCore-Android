package tech.mabase.www.adapt;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class InstallService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MODULE_CHECK = "android.intent.action.MODULE_CHECK";
    private static final String ACTION_PARSER_IDENTIFY = "android.intent.action.PARSER_IDENTIFY";
    private static final String ACTION_SKILL_INSTALL = "android.intent.action.ADAPT_SKILL_INSTALL";
    //If Mycroft determines this is a new module, register it in the database
    private static final String ACTION_MODULE_INSTALL = "android.intent.action.MODULE_INSTALL";
    //The Authority for Mycrofts database
    private static final String AUTHORITY = "www.mabase.tech.mycroft";
    //The Uri for Mycroft Module database
    private static final Uri MODULE_URI = Uri.parse("content://"+AUTHORITY+"/module");


    public InstallService() {
        super("InstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Adapt","Service was started");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSER_IDENTIFY.equals(action)) {
                Log.i("Adapt","It was receieved");
                Toast.makeText(this, "ID was received",Toast.LENGTH_SHORT).show();
                handleParserIdentify();
            } else if (ACTION_MODULE_INSTALL.equals(action)){
                handleParserInstall();
            }
        }
    }

    private void handleParserInstall(){
        Toast.makeText(this, "Received Adapt install message",Toast.LENGTH_SHORT).show();
        Log.i("Adapt Install","received the install message");
        /*
        Install the package name, the Adapt Skill ID, and type parser
         */
        ContentValues module= new ContentValues(3);
        module.put("package","tech.mabase.www.adapt");
        module.put("skill","android.intent.action.ADAPT_SKILL_IDENTIFY");
        module.put("type","parser");

        Log.i("Adapt Install","About to install to DB");
        Toast.makeText(this, "About to install to DB",Toast.LENGTH_SHORT).show();

        //Send it to mycroft
        getContentResolver().insert(MODULE_URI, module);
    }

    private void handleParserIdentify() {
        //This needs to pass android.intent.action.ADAPT_SKILL_IDENTIFY to Mycroft
        Log.i("Adapt Parser", "Parser identified");
        Toast.makeText(this, "Parser Identified",Toast.LENGTH_SHORT).show();
        String MYCROFT_PACKAGE = "www.mabase.tech.mycroft";
        String MYCROFT_CLASS = "MycroftService";
        //What is the purpose of module check?
        try {
            Intent check = new Intent();
            //This should allow dynamic package names
            check.setComponent(ComponentName.createRelative(MYCROFT_PACKAGE,MYCROFT_PACKAGE+"."+MYCROFT_CLASS));
            //Let core know who I am
            check.putExtra("package","tech.mabase.www.adapt");
            check.setAction(ACTION_MODULE_CHECK);
            startService(check);
        } catch (Exception e) {
            Log.e("Adapt Identify", "Couldn't seem tell Mycroft to start install process");
        }
    }
}
