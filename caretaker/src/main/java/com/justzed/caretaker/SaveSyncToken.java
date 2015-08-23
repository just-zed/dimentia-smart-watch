package com.justzed.caretaker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;
import com.parse.ParseObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Shirin on 8/22/2015.
 */
public class SaveSyncToken {

    private final Activity activity;

    public SaveSyncToken(Activity activity){
        this.activity = activity;
    }

    public String findDeviceId(){
        final TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, androidId;
        tmDevice = "" + tm.getDeviceId();
        androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32));
        String deviceId = deviceUuid.toString();

        return deviceId;
    }


    //get the token from patient device
    // tokensenderActivity token;
    // token.....
    public String patientDeviceID(){
        //String PatientDeviceID =
        return "";
    }

    String careTakerId=findDeviceId();
    String patientId=patientDeviceID();
    public void inserToDB(String careTakerId, String patientId){
        //ParseObject personLink = new ParseObject("PersonLink");
        //personLink.put("caretakerUniqueToken","deviceId" );
        // personLink.put("patientUniqueToken", "patientToken");

        // personLink.saveInBackground();


        Person patient = Person.getByUniqueToken(patientId).toBlocking().first();
        Person caretaker = Person.getByUniqueToken(careTakerId).toBlocking().first();

        PatientLink patientLink = new PatientLink(patient, caretaker).save().toBlocking().single();

        /*
        Person patient1 = patientLink.getPatient();
        Person caretaker1 = patientLink.getCaretaker();

        if (patient1.getType() == Person.PATIENT){
            //do something
        }

        patient1.getUniqueToken();
        caretaker1.getUniqueToken();


        PatientLink patientLink1 = PatientLink.getByPatient(patient).toBlocking().single();
        */
    }
}