package com.justzed.patient;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Tristan on 16/10/2015.
 */
public class TokenSenderHelper {

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
    }
}


