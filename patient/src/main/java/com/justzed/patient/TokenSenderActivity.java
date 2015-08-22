package com.justzed.patient;

import com.justzed.common.model.Person;
import android.app.Activity;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
//import com.parse.ParseObject;
//import com.parse.ParseClassName;

/**
 * Created by Tristan on 13/08/2015.
 *
 *
 */
public class TokenSenderActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback{
    //Private Constant
    private final String TEMPORARY_UNIQUE_ID = "myFirsUniqueToken";

    //Private Variables
    private NfcAdapter mNfcAdapter;
    private String myInformation;
    private String tempPatientInformation;
    private Person personDatabase;

    /**
     * Main method to send the myInformation's info to the caretaker
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sendPatientDataWithNFC();
    }

    /**
     * Created by Tristan
     *
     * Main method used to send the myInformation's info to the caretaker.
     */
    public void sendPatientDataWithNFC() {
        // Check for available NFC Adapter
        try{
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        catch(Exception e){
            //Try and catch used to avoid any errors with Toast and the tests
            try {
                Toast.makeText(TokenSenderActivity.this, "There was an error when attempting to use NFC.", Toast.LENGTH_LONG).show();
            } catch(Exception Toast){}

            finish();
            return;
        }

        if(!checkNfc(mNfcAdapter))
        {
            finish();
            return;
        }

        //Get the the user's data from the database
        myInformation = getMyRecordsFromDatabase(TEMPORARY_UNIQUE_ID);

        //Send the data via NFC
        mNfcAdapter.setNdefPushMessageCallback(this, this);
       Toast.makeText(TokenSenderActivity.this, "Connection completed!", Toast.LENGTH_LONG).show();
    }

    //Helper Methods
    /**
     * Created by Tristan
     *
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
        }
        catch(Exception e)
        {
            //Try and catch used to avoid any errors with Toast and the tests
            try{
            Toast.makeText(TokenSenderActivity.this, "Check that your device is NFC Compatible.", Toast.LENGTH_LONG).show();
            } catch(Exception Toast){}

            return false;
        }
    }

    /**
     * Created by Tristan
     *
     * NFC Method needed to get NfcAdapter.setNdefPushMessageCallback() to work.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event){
        return messageBuilder(myInformation);
    }

    /**
     * Created by Tristan
     *
     * Stores a string array inside an NDEF message.
     */
    public NdefMessage messageBuilder(String patientInformation){

        NdefMessage message = new NdefMessage(
                new NdefRecord[]{ NdefRecord.createMime("com.justzed.caretaker",
                        patientInformation.getBytes()),
                        NdefRecord.createApplicationRecord("caretaker")
                });

        return message;

    }

    /**
     * Created by Tristan
     *
     * Returns a string[] that contains information from the People table of the
     * Parse.com database using this device's unique id.
     */
    private String getMyRecordsFromDatabase(String tokenFromDatabase)
    {
        Person mySelf =
        new Person(Person.PATIENT,tokenFromDatabase)
                .save()
                .toBlocking()
                .first();

        tempPatientInformation = mySelf.getUniqueToken();


        return tempPatientInformation;
    }
}
