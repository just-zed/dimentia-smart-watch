package com.justzed.patient;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.justzed.common.SaveSyncToken;

import java.util.zip.Inflater;

/*
 * This class is used to send a pre-made message to the caretaker
 * @author Hiroki Takahashi
 * @since 6-10-2015
 */
public class PremadeMessagesActivity extends Activity {
    ListView list1;
    private final String PRE_MADE_MESSAGE_ONE = "Hello";
    private final String PRE_MADE_MESSAGE_TWO = "Call me";
    private final String PRE_MADE_MESSAGE_THREE = "The cat ate the dog";
    private final String PRE_MADE_MESSAGE_FOUR = "I have found your wallet";

    private String[] messages = {PRE_MADE_MESSAGE_ONE, PRE_MADE_MESSAGE_TWO, PRE_MADE_MESSAGE_THREE,
    PRE_MADE_MESSAGE_FOUR};


    // temp token
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premade_messages);

        list1 = (ListView) findViewById(R.id.list);
        list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages));

        list1.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        //Object o = list1.getItemAtPosition(position);

                            String message = messages[position];

                            // push to channel, channel name is patient's unique id
                            // channel name must start with letter
                            String channelName = "patient-" + getToken();
                            NotificationMessage.sendMessage(channelName, message);
                            Toast.makeText(getApplicationContext(), "A message has been sent: " + message, Toast.LENGTH_LONG).show();

                    }
                }
        );


    }

        /*
         * Used to get the token information from the res file
         * @param
         * @pre
         * @post  The token ID will be returned
         */
        private String getToken() {
            if (token != null) {
                return token;
            } else {
                String debugToken = getString(R.string.DEVICE_TOKEN);
                if (!TextUtils.isEmpty(debugToken)) {
                    return debugToken;
                } else {
                    return new SaveSyncToken(this).findMyDeviceId();
                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_premade_messages, menu);
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
