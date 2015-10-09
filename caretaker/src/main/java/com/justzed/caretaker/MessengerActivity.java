package com.justzed.caretaker;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justzed.common.model.Person;

public class MessengerActivity extends Activity {
    private EditText message;
    private Button sendButton;
    private TextView recipientName;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkIfSendButtonNeeded(message.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        recipientName = (TextView) findViewById(R.id.recipient_name);
        /**TODO change to name*/
        try {
            Bundle data = getIntent().getExtras();
            Person myPatient = data.getParcelable(Person.PARCELABLE_KEY);
            recipientName.setText(myPatient.getUniqueToken());
        }
        catch(Exception e){
            String NO_PATIENT = "No recipients could be found.";
            recipientName.setText("No Recipient");
            toast(NO_PATIENT);
        }


        sendButton = (Button) findViewById(R.id.send_button);
        message = (EditText) findViewById(R.id.edit_message);

        //set listeners
        message.addTextChangedListener(textWatcher);
        checkIfSendButtonNeeded(message.getText().toString());

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(message.getText().toString());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messenger, menu);
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

    public void checkIfSendButtonNeeded(String wordsInTextBox){
        if(wordsInTextBox.isEmpty()) {
            sendButton.setEnabled(false);
        }
        else{
            sendButton.setEnabled(true);
        }
    }


    public void sendMessage(String messageToSend){

        /**TODO add a mechanism for the caretaker to be able to send notifications from his side.*/
        try{
            //NotificationMessage.sendMessage(channelName, getString(R.string.exited_fence_notificiation));
            String MESSAGE_SUCCESS = "Your message was sent successfully.";
            toast(MESSAGE_SUCCESS);
            message.setText("");

        }
        catch(Exception toastException)
        {
            String MESSAGE_FAILED = "There was a problem sending your message. Please try again.";
            toast(MESSAGE_FAILED);
        }
    }

    private void toast(String toastMessage) {
        try {
            Toast.makeText(MessengerActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception toast) {
        }
    }
}
