package tech.mabase.www.mycroftlistenskill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View v){
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
}
