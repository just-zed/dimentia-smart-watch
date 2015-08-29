package com.justzed.patient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.view.View;
import android.view.View.OnClickListener;

import com.justzed.common.ApiKeys;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class PanicButtonActivity extends Activity {
    Button button;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_button);

        //Add Parse API keys
        Parse.initialize(this, ApiKeys.PARSE_API_TEST_APPLICATION_ID, ApiKeys.PARSE_API_TEST_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        //Enable Push notifications
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener( new OnClickListener() {
            public void onClick(View view) {
                textView.setText("You clicked the button!");
                NotificationMessage nm = new NotificationMessage();

                nm.setMessage("Test message from Hiroki");
                nm.sendMessage();

            }
        }
        );
        textView = (TextView) findViewById(R.id.textView1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_panic_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
