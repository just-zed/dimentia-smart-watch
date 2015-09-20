package com.justzed.patient;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

import com.justzed.common.model.Person;
//import com.parse.ParseObject;
//import com.parse.ParseClassName;

/**
 * This class is used to push an NDEF Message to the Caretaker's device.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-08-13
 */
public class TokenSenderActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    //Private Variables
    private NfcAdapter mNfcAdapter;

    private String myUniqueToken;

    /**
     * Main method to send the myInformation's info to the caretaker
     *
     * @param savedInstanceState This is a Bundle.
     * @return Nothing.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        Person person = data.getParcelable(Person.PARCELABLE_KEY);
        if (person != null) {
            myUniqueToken = person.getUniqueToken();
        } else {
            //somethings wrong, just kill the activity
            finishWithSuccess(false);
        }
        sendPatientDataWithNFC();
    }

    /**
     * Main method used to send the myInformation's info to the caretaker.
     *
     * @return Nothing.
     */

    public void sendPatientDataWithNFC() {
        // Check for available NFC Adapter
        try {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        } catch (Exception e) {
            //Try and catch used to avoid any errors with Toast and the tests
            try {
                Toast.makeText(TokenSenderActivity.this, "There was an error when attempting to use NFC.", Toast.LENGTH_LONG).show();
            } catch (Exception Toast) {
            }

            finishWithSuccess(false);
            return;
        }

        if (!checkNfc(mNfcAdapter)) {
            finishWithSuccess(false);
            return;
        }


        //Send the data via NFC
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    //Helper Methods

    /**
     * Checks wether the device has an NFC connection.
     *
     * @param deviceNfcAvailibility This is an NFC Adapter
     * @return boolean This returns whether the device is NFC compatible and whether tje NFC is enabled.
     */
    public boolean checkNfc(NfcAdapter deviceNfcAvailibility) {
        try {
            if (deviceNfcAvailibility == null) {
                Toast.makeText(TokenSenderActivity.this, "NFC is not available or has not been enabled for this device.", Toast.LENGTH_LONG).show();
                return false;

            } else {
                return true;
            }
        } catch (Exception e) {
            //Try and catch used to avoid any errors with Toast and the tests
            try {
                Toast.makeText(TokenSenderActivity.this, "Check that your device is NFC Compatible.", Toast.LENGTH_LONG).show();
            } catch (Exception Toast) {
            }

            return false;
        }
    }

    /**
     * NFC Method needed to get NfcAdapter.setNdefPushMessageCallback() to work.
     *
     * @param event This is an Nfc Event.
     * @return NdefMessage This is an Ndef message containing the unique token.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return messageBuilder(myUniqueToken);
    }

    /**
     * Stores a string array inside an NDEF message.
     *
     * @param patientInformation This is a string containing the patient's information
     * @return NdefMessage This returns an NdefMessage containing patientInformation.
     */
    public NdefMessage messageBuilder(String patientInformation) {

        return new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime("application/vnd.com.justzed.caretaker",
                        patientInformation.getBytes())
                });
//                        NdefRecord.createApplicationRecord("com.justzed.caretaker")

    }

    /**
     * Show a toast message when the Ndef push message is complete
     *
     * @param event This is the Nfc Event being checked for completion
     * @return Nothing.
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Toast.makeText(TokenSenderActivity.this, "Connection completed!", Toast.LENGTH_LONG).show();
        finishWithSuccess(true);
    }

    /**
     * Check if the connection was succesful
     *
     * @param status This is the status of the connection.
     * @return Nothing.
     */
    private void finishWithSuccess(boolean status) {
        this.setResult((status) ? RESULT_OK : RESULT_CANCELED);
        finish();
    }
}
