package com.justzed.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.justzed.common.DeviceUtils;
import com.justzed.common.NotificationMessage;
import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;
import com.parse.ParsePush;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    private Person patient;

    public static final String PREF_PERSON_KEY = "PersonPref";

    private static final int REQ_CODE_SEND_TOKEN = 1;  // The request code

    // temp token
    private String token;

    @Bind(R.id.panic_button)
    ImageButton panicButton;

    @Bind(R.id.message_button)
    ImageButton messageButton;

    @OnClick(R.id.panic_button)
    void onClickPanicButton() {
        if (patient != null) {
            // push to channel, channel name is patient's unique id
            // channel name must start with letter
            String channelName = "patient-" + getToken();
            NotificationMessage.sendMessage(channelName, String.format(getString(R.string.panic_message), patient.getName()));
            Toast.makeText(getApplicationContext(), R.string.caretakers_alerted, Toast.LENGTH_SHORT).show();

        }
    }

    @OnClick(R.id.message_button)
    void startMessageActivity() {
        if (patient != null) {
            Intent intent = new Intent(this, PremadeMessagesActivity.class);
            intent.putExtra(Person.PARCELABLE_KEY, patient);
            startActivity(intent);
        }
    }

    @OnClick(R.id.add_caretaker_button)
    void onAddCareTakerButtonClick() {
        if (patient != null) {
            Intent intent = new Intent(this, TokenSenderActivity.class);
            intent.putExtra(Person.PARCELABLE_KEY, patient);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        panicButton.setEnabled(false);
        messageButton.setEnabled(false);


        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!mPrefs.contains(PREF_PERSON_KEY)) {
            //create patient and save

            token = getToken();

            Person personToSave = new Person(Person.PATIENT, token);
            personToSave.setName(DeviceUtils.getDeviceOwnerName(getApplication(),
                    getString(R.string.default_patient_name)));

            personToSave
                    .save()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(person -> {
                        // save patient in app
                        this.patient = person;
                        panicButton.setEnabled(true);
                        messageButton.setEnabled(true);

                        Editor editor = mPrefs.edit();
                        editor.putString(PREF_PERSON_KEY, person.getUniqueToken());
                        editor.apply();
                        //start token activity
                        autoStartTokenSenderActivity();
                    }, throwable -> {
                    });


        } else {
            //get patient token from cache, get person object from database and start service
            token = mPrefs.getString(PREF_PERSON_KEY, "");

            Person.findByUniqueToken(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            person -> {
                                this.patient = person;

                                panicButton.setEnabled(true);
                                messageButton.setEnabled(true);

                                startPatientService();
                                //start token activity
                                autoStartTokenSenderActivity();

                            },
                            throwable -> {
                                Editor editor = mPrefs.edit();
                                editor.remove(PREF_PERSON_KEY);
                                editor.apply();
                                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                    );

        }


    }

    private void startPatientService() {
        //start service
        Intent serviceIntent = new Intent(this, PatientService.class);
        serviceIntent.putExtra(Person.PARCELABLE_KEY, patient);
        startService(serviceIntent);
        //subscribe to caretaker notifications
        ParsePush.subscribeInBackground("caretaker-" + getToken());

    }

    private void autoStartTokenSenderActivity() {
        if (patient != null) {
            //TODO: move these to repository class
            //only do this if the patient link does not exist
            PatientLink.findLatestByPatient(patient)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(patientLink -> {
                        if (patientLink == null) {
                            Intent intent = new Intent(this, TokenSenderActivity.class);
                            intent.putExtra(Person.PARCELABLE_KEY, patient);
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
            String debugToken = getString(R.string.DEVICE_TOKEN);
            if (!TextUtils.isEmpty(debugToken)) {
                token = debugToken;
                return token;
            } else {
                token = new SaveSyncToken(this).findMyDeviceId();
                return token;
            }
        }

    }

}
