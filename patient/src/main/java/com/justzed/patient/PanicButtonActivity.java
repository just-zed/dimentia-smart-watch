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
    private final ParsePush push = new ParsePush();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_button);

        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener( new OnClickListener() {
            public void onClick(View view) {
                textView.setText("Message is sent");

                sendMessage("Test message from PanicButtonActivity");
            }
        }
        );
        textView = (TextView) findViewById(R.id.textView1);
    }

    public void sendMessage(String message) {
        push.setMessage(message);
        push.sendInBackground();

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
