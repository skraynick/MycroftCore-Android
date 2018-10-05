package www.mabase.tech.mycroft;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.spec.ECField;
import java.util.List;
import java.util.ServiceConfigurationError;

import static java.security.AccessController.getContext;

/**
 * Copyright (C) 2017 Christopher Carroll
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        This is a simple version of user input for testing the application. Eventually it will be replaced by the SpeakSkill module
    */
    public void submit(View v){

        EditText input = (EditText)findViewById(R.id.utterance);
        TextView output = (TextView)findViewById(R.id.output);
        String utterance = input.getText().toString();

        output.setText(utterance);
        input.setText("");

        /*
        Send a parse intent for all parsers. Later this will be a for loop(?) but for now, just work with Adapt
         */
        Intent parse = new Intent();
        parse.setClass(this, MycroftService.class);
        parse.setAction("android.intent.action.MYCROFT_PARSE");
        parse.putExtra("utterance", utterance);
        try{
            startService(parse);
        } catch (Exception e){
            Log.i("Mycroft.submit()", "Something is wrong with MycroftService");
        }
    }

    // This queries all packages to make sure all parsers are installed, Then it queries
    // to make sure all skills are installed
    public List getPackageList(Context context, String action){

        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentServices(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

        return list;
    }

    /*
    This needs to take the list of parsers and check if there are any others installed
    */
    public void update(View v) {
        final String IDENTIFY = "android.intent.action.PARSER_IDENTIFY";

        List list = getPackageList(this.getApplicationContext(), IDENTIFY);
        int size = list.size();

        Log.i("Mycroft Core", "Querying package manager: Size "+size);
        for(int i = 0; i < size; i++){
            Log.i("Mycroft Core", list.toString());
        }
    }

    public void startMycroft(View v){
        Log.i("Mycroft","startMycroft() is a work in progress");
        Intent mycroft = new Intent();
        mycroft.setClass(this, MycroftService.class);
        startService(mycroft);
    }

    public void stopMycroft(View v){
        Log.i("Mycroft","stopMycroft() not yet implemented");

        Intent mycroft = new Intent();
        mycroft.setClass(this, MycroftService.class);
        stopService(mycroft);
    }


}
