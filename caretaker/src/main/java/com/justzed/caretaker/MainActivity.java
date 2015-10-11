package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import com.justzed.common.DeviceUtils;
import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;
import com.parse.ParsePush;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Person caretaker;
    private Person patient;

    public static final String PREF_PERSON_KEY = "PersonPref";

    // temp token
    private String token;

    @Bind(R.id.button)
    Button button;

    @Bind(R.id.button_messenger)
    Button buttonMessenger;

    @Bind(R.id.switch_patient_disable_check)
    Switch switchPatientDisableCheck;

    @OnClick(R.id.button)
    void mapButtonClick() {
        if (patient != null) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(Person.PARCELABLE_KEY, patient);
            startActivity(intent);
        }
    }

    @OnClick(R.id.button_messenger)
    void messengerButtonClick() {

        Intent intent = new Intent(this, MessengerActivity.class);
        intent.putExtra(Person.PARCELABLE_KEY, patient);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        button.setEnabled(false);
        buttonMessenger.setEnabled(false);
        switchPatientDisableCheck.setEnabled(false);

        //TODO: move these to a splash screen activity?
        getCaretaker()
                .flatMap(PatientLink::findLatestByCaretaker)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(patientLink -> {
                    this.patient = patientLink.getPatient();
                    return patientLink;
                })
                .subscribe(
                        patientLink -> {
                            // subscribe to patient's push channel
                            finishActivityWithResult();
                        },
                        throwable -> Log.e(TAG, throwable.getMessage()));


    }

    private void toggleSubscription(boolean subscribe) {
        if (patient != null) {
            String channelName = "patient-" + patient.getUniqueToken();
            if (subscribe) {
                ParsePush.subscribeInBackground(channelName);
            } else {
                ParsePush.unsubscribeInBackground(channelName);
            }
        }
    }


    @NonNull
    private Observable<Person> getCaretaker() {
        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!mPrefs.contains(PREF_PERSON_KEY)) {
            //create patient and save
            token = getToken();

            Person caretaker = new Person(Person.CARETAKER, token);
            caretaker.setName(DeviceUtils.getDeviceOwnerName(getApplication(),
                    getString(R.string.default_caretaker_name)));

            return caretaker
                    .save()
                    .map(person1 -> {
                        this.caretaker = person1;
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString(PREF_PERSON_KEY, person1.getUniqueToken());
                        editor.apply();
                        return person1;
                    });
        } else {
            //get patient token from app data,
            // get person object from database and start service
            token = mPrefs.getString(PREF_PERSON_KEY, "");

            return Person.findByUniqueToken(token)
                    .map(person1 -> {
                        this.caretaker = person1;
                        return person1;
                    });
        }
    }

    private void finishActivityWithResult() {
        if (patient != null) {
            button.setEnabled(true);
            buttonMessenger.setEnabled(true);
            switchPatientDisableCheck.setEnabled(true);

            button.setText(String.format(getString(R.string.find_my_patient_button_text), patient.getName()));
            buttonMessenger.setText(String.format(getString(R.string.message_my_patient_button_text), patient.getName()));
            switchPatientDisableCheck.setText(String.format(getString(R.string.patient_nearby_text), patient.getName()));

            switchPatientDisableCheck.setChecked(patient.getDisableGeofenceChecks());
            toggleSubscription(!patient.getDisableGeofenceChecks());

            switchPatientDisableCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                patient.setDisableGeofenceChecks(isChecked);

                patient.save().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(person -> {
                            //do nothing
                        }, throwable -> Log.e(TAG, throwable.getMessage()));
            });
        }


        // if activity is called by NfcActivity, close and return result
        if (getCallingActivity() != null && caretaker != null) {
            Intent result = new Intent();
            result.putExtra(Person.PARCELABLE_KEY, caretaker);
            setResult(RESULT_OK, result);
            finish();
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