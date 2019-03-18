package www.mabase.tech.mycroft;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;

import www.mabase.tech.mycroft.mycroftstt.PocketSphinxService;

/**
 * This service ensures that Mycroft is always running and always available. It also offers a simple CLI for the user.
 */

public class MycroftService extends Service {

    private static final String ACTION_PARSE_FINISHED = "android.intent.action.PARSE_FINISHED";
    private static final String ACTION_MYCROFT_PARSE = "android.intent.action.MYCROFT_PARSE";
    private static final String ACTION_MODULE_CHECK = "android.intent.action.MODULE_CHECK";

    // Target we public for clients to send messages to IncomingHandler
    Messenger mMessenger;

    public MycroftService() {
    }

    // I am defining a custom Handler, with its own handleMessage method
    private static Handler IncomingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            /*
            Use this space to handle incoming messages. That being said, I don't know what messages
            I am expecting to Handle? This is where I can redirect the data flow
            */
            // Get the bundle and extract the message
            Bundle bundle = msg.getData();
            // I should wrap this in a try catch
            Log.i("MycroftService", (String)bundle.get("Message"));
            /*
            if(msg == "Utterance"){
                parseUtterance(msg);
            }else if(msg == "Parse Finished"){
                handleParserFinished(msg.getParser());
            }else if(msg == "New Mycroft Plugin"){
                installPlugin(msg.getPluginDetails());
             */

        }
    };
    @Override
    public IBinder onBind(Intent intent) {

        /*
        This returns a binder to the service asking to bind with MycroftService. I give them my
        custom handler IncomingHandler, which they will send messages to
        */

        mMessenger = new Messenger(IncomingHandler);
        /*
        This sends them the Binder for mMessenger, so when the send it a message it comes to
        MycroftService
         */
        return mMessenger.getBinder();
    }

    //This service needs to be able to 'hear' the responses from parsers
    /*
    The service listens for responses from the parser.
    THIS SHOULD BE REPLACED WITH AN INCOMING HANDLER
     */
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

    //This is catching MycroftActivity CLI and VoiceService utterances for parsing.
    private void parseUtterance(String Utterance) {
       /* // Create and send a message to the service, using a supported 'what' value
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
       */
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

        /*
        This section starts all the needed external components for Mycroft to work, namely the STT
        service, and the parser. Right now they are static, but eventually the will be replaced with
        a database lookup so they can be changed in the settings menu. These services by default
        will bind to MycroftService upon startup
         */
        try {
            Intent startSTT = new Intent();
            //This should allow dynamic package names
            startSTT.setClass(this, PocketSphinxService.class);
            startSTT.setAction("mycroft.STT_START");
            startService(startSTT);
        } catch (Exception e) {
            Log.e("MycroftService", "Couldn't seem to start MycroftSTT");
        }

        /*
        Here it should bind up to 4 parser services
        request from the core database all parser package names.
         */
        String PARSE_PACKAGE = "tech.mabase.www.adapt";
        String PARSE_CLASS = "AdaptParser";
        try {
            Intent parserInit = new Intent();
            //This should allow dynamic package name
            parserInit.setComponent(ComponentName.createRelative(PARSE_PACKAGE,PARSE_PACKAGE+"."+PARSE_CLASS));
            // Don't forget to start the service
        } catch (Exception e) {
            Log.e("MycroftService", "Couldn't start MycroftParser");
        }
    }


    @Override
    public void onDestroy() {
        /*
        Here, it needs to unbind everything that is bound to it
         */

        Toast.makeText(this, "Mycroft service terminated", Toast.LENGTH_SHORT).show();
    }
}
