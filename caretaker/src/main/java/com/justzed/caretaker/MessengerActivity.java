package com.justzed.caretaker;

import android.app.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justzed.common.NotificationMessage;
import com.justzed.common.model.Person;
/**
 * This activity runs the messenging system on the caretaker's device.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-09-29
 */
public class MessengerActivity extends Activity {
    private EditText message;
    private Button sendButton;
    private TextView recipientName;
    Person myPatient;
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


    /**
     *The main method of this activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        recipientName = (TextView) findViewById(R.id.recipient_name);

        /**TODO change to name*/
        try {
            Bundle data = getIntent().getExtras();
            myPatient = data.getParcelable(Person.PARCELABLE_KEY);
            recipientName.setText(myPatient.getName());
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

    /**
     * Checks if the textbox contains any words in it and
     * disables the send button accordingly.
     *
     * @param wordsInTextBox a string of the words in the textbox being checked.
     * @return Nothing.
     */
    public void checkIfSendButtonNeeded(String wordsInTextBox){
        if(wordsInTextBox.isEmpty()) {
            sendButton.setEnabled(false);
        }
        else{
            sendButton.setEnabled(true);
        }
    }

    /**
     * sends a notification containing a message to another a patient
     *
     * @param messageToSend a string containing a message.
     * @return Nothing.
     */
    public void sendMessage(String messageToSend){
        String channelName = "caretaker-" + myPatient.getUniqueToken();

        /**TODO add a mechanism for the caretaker to be able to send notifications from his side.*/
        try{
            NotificationMessage.sendMessage(channelName, messageToSend);
            String MESSAGE_SUCCESS = "A message has been sent: " + messageToSend;
            toast(MESSAGE_SUCCESS);
            message.setText("");

        }
        catch(Exception toastException)
        {
            String MESSAGE_FAILED = "There was a problem sending your message. Please try again.";
            toast(MESSAGE_FAILED);
        }
    }

    /**
     * shows a toast message on the screen.
     *
     * @param toastMessage  a string to be turned into a toast message.
     * @return Nothing.
     */
    private void toast(String toastMessage) {
        try {
            Toast.makeText(MessengerActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception toast) {
        }
    }
}
