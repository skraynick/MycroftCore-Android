package www.mabase.tech.mycroft;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class InstallReceiver extends BroadcastReceiver {

    public final String TAG = "Mycroft Core";
    private static final String ACTION_PARSER_IDENTIFY = "android.intent.action.PARSER_IDENTIFY";
    private static final String ACTION_SKILL_IDENTIFY = "android.intent.action.SKILL_IDENTIFY";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        When a new package is installed, ask package manager for all apps with
        X,Y,Z... intents. Check returned list against internal DB, update with
        uninstalled plug-ins. Query parse engines first, then query skills
         */

        //Get package ID and send explicit intent\
        // This package ID and name should be saved to query uninstall broadcasts
        Log.i(TAG, "Install broadcast received by NAVCORE");

        //This gets the package name of the new package
        Bundle b = intent.getExtras();
        int uid = b.getInt(Intent.EXTRA_UID);
        String[] packages = context.getPackageManager().getPackagesForUid(uid);
        Log.i(TAG, "found package "+packages[0]);

        String bug = packages[0];

        //Don't make this so specific
        Intent parserId = new Intent();
        parserId.setAction(ACTION_PARSER_IDENTIFY);
        parserId.setComponent(ComponentName.createRelative(packages[0],packages[0]+".InstallService"));
        try {
            context.startService(parserId);
            Log.i(TAG,"PARSER_IDENTIFY intent sent");
        }catch(Exception e){
            Log.e(TAG, "There is no receiving engine");
        }

        /*
        Here, query the PackageManager for all installed packages w/ intent
        MYCROPT_PARSER_IDENTIFY. save in pm[]
        check if new package is in list. If so, install

        if not:
            query for all SKILL_IDENTIFIERS
            check package name against skill identifiers.
            if it is found, explicit intent to PARSER_SKILL_INSTALL w/ package name in bundle

            When updating externally, go through same processes, except check skills after installing
            parser, rather than stopping after parser.

            I want to reduce the amount of queries taking place. Update could be a separate
            method that can be called separately by the update settings button
         */

        /*
        //Build an intent explicitly for the newly installed package.
        Intent i = new Intent();
        i.setAction(ACTION_PARSER_IDENTIFY);
        i.setClassName(packages[0], packages[0]+".InstallService");
        Log.i(TAG,ACTION_PARSER_IDENTIFY);
        Log.i(TAG,packages[0]+".InstallService.class");

        //This prevents Mycroft from crashing if there isn't a receiving intent
        try {
            context.startService(i);
            Log.i(TAG,"PARSER_IDENTIFY intent sent");
        }catch(Exception e){
            Log.e(TAG, "There is no receiving engine");
        }
        */
    }
}
