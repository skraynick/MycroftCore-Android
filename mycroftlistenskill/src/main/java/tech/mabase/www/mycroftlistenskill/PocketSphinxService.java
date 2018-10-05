package tech.mabase.www.mycroftlistenskill;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import android.util.Log;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class PocketSphinxService extends Service implements RecognitionListener {
    public PocketSphinxService() {
    }

    private static final String ACTION_SKILL_CALL = "android.intent.action.SKILL_CALL";

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FREE_SPEECH = "free";

    /* Keyword we are looking for to activate menu. Change this to my app name
     * Maybe even load the app name in based on the main Mycroft settings. However it might
      * need to update the dictionary*/
    private static final String KEYPHRASE = "hey mega man";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /*
    This binds to Mycroft core when it starts, and in turn binds Mycroft Core to it, so that it can
    send PARSE_UTTERANCE messages to it (or should that stay a generic broadcast for other intent persers,
    such as the command line interface???
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding PocketSphinx", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    /*
    When being used as a hotword listener, it should be bound by the lifecycle of Mycroft Core, which
    means that it shouldn't wakelock unless the setting is specifically set by Core. When it is being
    called in a specific context for a skill, it should stop the general listening service, and
    start a specific one based on the needs of the skill.

    THis concept could mean that the Skill should be a content provider, so as to offer a new dictionary
    to PocketSphinx, although I am inclined to believe this adds a level of unneeded complexity..
     */

    //This module shound't be receiving messages. However, if it does, this is where they will be
    //handled. I might need to bind it later for a "back and fourth" between a skill though...
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MycroftListener","This module is only bound to keep it running. It should not be receiving messages");
        }
    }

    /*PocketSphinx should be started when it is bound by Mycroft Core. However, it might need to
    run a one off voice recognition for a skill, so the skill should run a
    startService(MycroftListen) which will cause the main service to stop, run a once off voice
    recognition service, and then restart the standard listening protocol. Maybe I need to temporaraly
    bind the service
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            //This could be changed to a bound service message that checks the process ID against the
            //initial bound parsers. When all have responded, the global verifier can be reset.
            if (ACTION_SKILL_CALL.equals(action)) {
                handleSkillCall();
            }
        }
        return START_STICKY;
    }

    //When a skill just needs to call the voice service once, it's called here
    public void handleSkillCall() {
        Log.i("MycroftListen","Handeling a skill call");
    }

    public void onCreate() {
        super.onCreate();

        //There needs to be implemented a permission check before this is initalized, otherwise the
        //system could crash, or users wouldn't know why it isn't working
        new SetupTask(this).execute();
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<PocketSphinxService> serviceReference;
        SetupTask(PocketSphinxService service) {
            this.serviceReference = new WeakReference<>(service);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(serviceReference.get());
                File assetDir = assets.syncAssets();
                serviceReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                Log.e("MycroftListen", "Failed to init recognizer " + result);
            } else {
                serviceReference.get().switchSearch(KWS_SEARCH);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    //We have it waiting for its, name. If it is found, listen to the generic speech
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            //Replace this with a generic speech listener (That listens for any word in the dictionary)
            switchSearch(FREE_SPEECH); //This needs to return the output to Mycroft, for parsing
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        //String caption = getResources().getString(captions.get(searchName));
        Toast.makeText(this, searchName, Toast.LENGTH_SHORT).show();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                //.setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        File ngramModel = new File(assetsDir, "en-us.lm.bin");
        recognizer.addNgramSearch(FREE_SPEECH, ngramModel);
    }

    @Override
    public void onError(Exception error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }
}
