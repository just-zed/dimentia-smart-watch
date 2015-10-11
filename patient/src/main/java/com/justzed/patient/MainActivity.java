package com.justzed.patient;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

import com.justzed.common.NotificationMessage;
import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    private Person person;

    public static final String PREF_PERSON_KEY = "PersonPref";

    private static final int REQ_CODE_SEND_TOKEN = 1;  // The request code

    // temp token
    private String token = "00000000-6c94-4036-0033-c58700000000";


    @Bind(R.id.panic_button)
    ImageButton panicButton;

    @OnClick(R.id.panic_button)
    void onClickPanicButton() {
        if (token != null) {
            // push to channel, channel name is patient's unique id
            // channel name must start with letter
            String channelName = "patient-" + getToken();
            NotificationMessage.sendMessage(channelName, getString(R.string.panic_message));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        token = getToken();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();


        panicButton.setEnabled(false);


        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!mPrefs.contains(PREF_PERSON_KEY)) {
            //create patient and save

            new Person(Person.PATIENT, getToken())
                    .save()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(person -> {
                        // save person in app
                        this.person = person;
                        panicButton.setEnabled(true);
                        Editor editor = mPrefs.edit();
                        editor.putString(PREF_PERSON_KEY, person.getUniqueToken());
                        editor.apply();
                        //start token activity
                        startTokenSenderActivity();
                    });


        } else {
            //get patient token from cache, get person object from database and start service
            String uniqueToken = mPrefs.getString(PREF_PERSON_KEY, "");

            Person.getByUniqueToken(uniqueToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            person -> {
                                this.person = person;
                                panicButton.setEnabled(true);
                                startPatientService();
                                //start token activity
                                startTokenSenderActivity();
                            },
                            throwable -> {
                            }
                    );

        }


    }

    private void startPatientService() {
        //start service
        Intent serviceIntent = new Intent(this, PatientService.class);
        serviceIntent.putExtra(Person.PARCELABLE_KEY, person);
        startService(serviceIntent);
    }

    private void startTokenSenderActivity() {
        if (person != null) {
            //TODO: move these to repository class
            //only do this if the patient link does not exist
            PatientLink.getByPatient(person)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(patientLink -> {
                        if (patientLink == null) {
                            Intent intent = new Intent(this, TokenSenderActivity.class);
                            intent.putExtra(Person.PARCELABLE_KEY, person);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivityForResult(intent, REQ_CODE_SEND_TOKEN);
                        }
                    }, throwable -> {
                    });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQ_CODE_SEND_TOKEN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                startPatientService();
            } else {
                //kill the app if tokenSender is returning error
                finish();
            }
        }
    }

    private String getToken() {
        if (token != null) {
            return token;
        } else {
            return new SaveSyncToken(this).findMyDeviceId();
        }
    }

}
