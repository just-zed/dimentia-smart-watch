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

    private Person person;


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
                        this.person = person;
                        Editor editor = mPrefs.edit();
                        editor.putString(PatientService.PREF_PATIENT_KEY, person.getUniqueToken());
                        editor.apply();

                        //start token activity
                        startTokenSenderActivity();
                    });


        } else {
            //get patient token from cache, get person object from database and start service
            String uniqueToken = mPrefs.getString(PatientService.PREF_PATIENT_KEY, "");

            Person.getByUniqueToken(uniqueToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            person -> {
                                this.person = person;

                                //start service
                                Intent serviceIntent = new Intent(this, PatientService.class);
                                serviceIntent.putExtra(PatientService.INTENT_PATIENT_KEY, uniqueToken);
                                startService(serviceIntent);

                                //start token activity
                                startTokenSenderActivity();
                            },
                            throwable -> {
                            }
                    );

        }


    }

    private void startTokenSenderActivity() {
        if (person != null) {
            //only do this if the patient link does not exist
            PatientLink.getByPatient(person)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(patientLink -> {
                        if (patientLink == null) {
                            Intent intent = new Intent(this, TokenSenderActivity.class);
                            intent.putExtra(TokenSenderActivity.INTENT_TOKEN_KEY, person.getUniqueToken());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivityForResult(intent, SEND_TOKEN);
                        }
                    }, throwable -> {
                    });
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
