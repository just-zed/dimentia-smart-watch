package com.justzed.patient;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    private final Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();


        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        if (!mPrefs.contains(PatientService.PREF_PATIENT_KEY)) {
            //create patient and save

            new Person(Person.PATIENT, generateToken())
                    .save()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(person -> {
                        // save person in app
                        Editor editor = mPrefs.edit();
                        String personJson = gson.toJson(person);
                        editor.putString(PatientService.PREF_PATIENT_KEY, personJson);
                        editor.apply();

                        //start service
                        Intent serviceIntent = new Intent(this, PatientService.class);
                        serviceIntent.putExtra(PatientService.INTENT_PATIENT_KEY, personJson);
                        startService(serviceIntent);

                        //start token activity
                        startTokenSenderActivity(personJson);
                    });


        } else {
            //get patient from cache and start service
            String personJson = mPrefs.getString(PatientService.PREF_PATIENT_KEY, "");
            //start service
            Intent serviceIntent = new Intent(this, PatientService.class);
            serviceIntent.putExtra(PatientService.INTENT_PATIENT_KEY, personJson);
            startService(serviceIntent);

            //start token activity
            startTokenSenderActivity(personJson);
        }


    }

    private void startTokenSenderActivity(String personJson) {
        Person person = gson.fromJson(personJson, Person.class);

        //only do this if the patient link does not exist
        if (PatientLink.getByPatient(person).toBlocking().single() == null) {
            Intent tokenSenderActivityIntent = new Intent(this, TokenSenderActivity.class);
            tokenSenderActivityIntent.putExtra(TokenSenderActivity.INTENT_TOKEN_KEY, person.getUniqueToken());
            startActivityForResult(tokenSenderActivityIntent, SEND_TOKEN);
        }

    }


    static final int SEND_TOKEN = 1;  // The request code


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SEND_TOKEN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)


            }
        }
    }

    private String generateToken() {
        return "patient";
    }

}
