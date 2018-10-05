package www.mabase.tech.mycroft;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * This service ensures that Mycroft is always running and always available. It also offers a simple CLI for the user.
 */

public class MycroftService extends Service {

    private static final String ACTION_PARSE_FINISHED = "android.intent.action.PARSE_FINISHED";
    private static final String ACTION_MYCROFT_PARSE = "android.intent.action.MYCROFT_PARSE";
    private static final String ACTION_MODULE_CHECK = "android.intent.action.MODULE_CHECK";

    public MycroftService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //This service needs to be able to 'hear' the responses from parsers
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            //This could be changed to a bound service message that checks the process ID against the
            //initial bound parsers. When all have responded, the global verifier can be reset.
            if (ACTION_PARSE_FINISHED.equals(action)) {
                handleParserFinished(intent.getStringExtra("parser"), intent.getStringExtra("response"));
            //This is triggered whenever a parse needs to occur. This could be changed to a
            //bound service message
            } else if(ACTION_MYCROFT_PARSE.equals(action)) {
                Toast.makeText(this, "Sending utterance "+intent.getStringExtra("utterance"), Toast.LENGTH_SHORT).show();
                parseUtterance(intent.getStringExtra("utterance"));
                //This is checking if it is a new module, or an existing module, for DB purposes
            } else if (ACTION_MODULE_CHECK.equals(action)) {
                String module = intent.getStringExtra("package");
                Toast.makeText(this, "Checking if module installed", Toast.LENGTH_SHORT).show();
                checkModule(module);
            }
        }
        return START_STICKY;
    }

    /*
    This method exists to check if the module is already installed.
    Is it needed? If so, why?

    Check module can be triggered by an update OR a new install, if it is an update than it needs
    to clear out old database entries, and replace them with new ones.
     */
    private void checkModule(String module){
        Log.i("MycroftService", "Checking module");
        Toast.makeText(this, "Checking new module",Toast.LENGTH_SHORT).show();
        //Replace this with a DB query, to see if the module already exists in the database
        //For now, this is just installing adapt.
        String ADAPT_PACKAGE = "tech.mabase.www.adapt";
        String ADAPT_CLASS = "InstallService";
        try {
            Intent install = new Intent();
            //This should allow dynamic package names
            install.setComponent(ComponentName.createRelative(ADAPT_PACKAGE,ADAPT_PACKAGE+"."+ADAPT_CLASS));
            install.setAction("android.intent.action.MODULE_INSTALL");
            Toast.makeText(this, "Starting module install",Toast.LENGTH_SHORT).show();
            startService(install);
            Log.i("MycroftService", "starting install module");
        } catch (Exception e) {
            Log.e("MycroftService", "Couldn't seem to start the module install service");
        }
    }

    //This counts the number of responding parsers. When all parsers have responded, it weighs the
    //responses. Perhaps it should be take with the IBinder. This is here instead of an
    //IntentService because it needs to count up all of the responses, which is a persistent variable
    private void handleParserFinished(String id, String response){
        /*
        takeParseInfo[n][0] = id;
        takeParseInfo[n][1] = response
        if(noAllIn){
            return;
        } else {
            decision = decide(takeParseInfo);
            executeSkill(decision);
        }
         */
        Toast.makeText(this, id+" response received. Intent "+response+" picked", Toast.LENGTH_LONG).show();
        //Execute the skill
        String SKILL_CLASS = "";
        String SKILL_PACKAGE = "";
        Intent skill = new Intent();
        skill.setComponent(ComponentName.createRelative(SKILL_PACKAGE,SKILL_PACKAGE+"."+SKILL_CLASS));
    }

    //This is catching MyscroftActivity CLI and VoiceService utterances for parsing.
    private void parseUtterance(String param1) {
        //This needs to be changed so it tries to bound the service if unbound
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain();
        //Set the what to parse
        msg.what = 1;
        //Create bundle with utterance data
        Bundle bundle = new Bundle();
        bundle.putString("utterance",param1);
        msg.setData(bundle);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    /*
    For this version of Android, I am stuck making the notification a read only
    text display. Once 7.0 becomes more prolific I will be able to add inline
    edit text, allowing for Mycroft CLI interaction
     */
    @Override
    public void onCreate() {
        Toast.makeText(this, "Mycroft service started", Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.assistant)
                        .setContentTitle("Mycroft")
                        .setContentText("Hello, User");

        startForeground(1225, mBuilder.build());
        //Start always listening voice service
        /*
        Check the Mycroft Module database to see if there are any modules marked for STT.
        Only one can be used at a time (for hotword listening). It should be marked in the Db
        as the default. Start the service
         */
        /*
        Bind PocketSpinx

         */
        String LISTEN_PACKAGE = "tech.mabase.www.mycroftlistenskill";
        String LISTEN_CLASS = "PocketSphinxService";
        try {
            Intent listenerInit = new Intent();
            //This should allow dynamic package names
            listenerInit.setComponent(ComponentName.createRelative(LISTEN_PACKAGE,LISTEN_PACKAGE+"."+LISTEN_CLASS));
            bindService(listenerInit, nConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.e("MycroftService", "Couldn't seem to bind Mycroft Listen Skill");
            Toast.makeText(getApplicationContext(), "Couldn't bind PocketSphinx", Toast.LENGTH_SHORT).show();
        }

        /*
        Here it should bind up to 4 parser services
        request from the core database all parser package names.
         */
        String ADAPT_PACKAGE = "tech.mabase.www.adapt";
        String ADAPT_CLASS = "AdaptParser";
        try {
            Intent parserInit = new Intent();
            //This should allow dynamic package names
            parserInit.setComponent(ComponentName.createRelative(ADAPT_PACKAGE,ADAPT_PACKAGE+"."+ADAPT_CLASS));
            bindService(parserInit, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.e("MycroftService", "Couldn't seem to bind Adapt");
        }
    }

    Messenger mService, nService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mBound, nBound;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    //This is a simple service copy, for the second bound service (Listener)
    private ServiceConnection nConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            nService = new Messenger(service);
            nBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            nService = null;
            nBound = false;
        }
    };

    @Override
    public void onDestroy() {

        /*
        Here it should unbind all active parser services
        for(selectedServices){
            unbind(service[i]);
        }
         */
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        Toast.makeText(this, "Mycroft service terminated", Toast.LENGTH_SHORT).show();
    }
}
