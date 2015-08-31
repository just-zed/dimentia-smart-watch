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
 * Created by Tristan on 13/08/2015.
 */
public class TokenSenderActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    //Private Variables
    private NfcAdapter mNfcAdapter;

    private String myUniqueToken;

    /**
     * Main method to send the myInformation's info to the caretaker
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
     * Created by Tristan
     * <p>
     * Main method used to send the myInformation's info to the caretaker.
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
     * Created by Tristan
     * <p>
     * Checks wether the device has an NFC connection.
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
     * Created by Tristan
     * <p>
     * NFC Method needed to get NfcAdapter.setNdefPushMessageCallback() to work.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return messageBuilder(myUniqueToken);
    }

    /**
     * Created by Tristan
     * <p>
     * Stores a string array inside an NDEF message.
     */
    public NdefMessage messageBuilder(String patientInformation) {

        return new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime("application/vnd.com.justzed.caretaker",
                        patientInformation.getBytes())
                });
//                        NdefRecord.createApplicationRecord("com.justzed.caretaker")

    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Toast.makeText(TokenSenderActivity.this, "Connection completed!", Toast.LENGTH_LONG).show();
        finishWithSuccess(true);
    }

    private void finishWithSuccess(boolean status) {
        this.setResult((status) ? RESULT_OK : RESULT_CANCELED);
        finish();
    }
}
