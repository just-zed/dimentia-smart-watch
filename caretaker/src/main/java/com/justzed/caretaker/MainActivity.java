package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

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
    private String token = "ffffffff-fcfb-6ccb-0033-c58700000000";

    @Bind(R.id.button)
    View button;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        button.setEnabled(false);
        switchPatientDisableCheck.setEnabled(false);

        //TODO: move these to a splash screen activity?
        getCaretaker()
                .flatMap(PatientLink::getByCaretaker)
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

            return new Person(Person.CARETAKER, getToken())
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
            String uniqueToken = mPrefs.getString(PREF_PERSON_KEY, "");

            return Person.getByUniqueToken(uniqueToken)
                    .map(person1 -> {
                        this.caretaker = person1;
                        return person1;
                    });
        }
    }

    private void finishActivityWithResult() {
        button.setEnabled(true);
        switchPatientDisableCheck.setEnabled(true);
        switchPatientDisableCheck.setChecked(patient.getDisableGeofenceChecks());
        toggleSubscription(!patient.getDisableGeofenceChecks());

        switchPatientDisableCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (patient != null) {
                patient.setDisableGeofenceChecks(isChecked);
                toggleSubscription(!isChecked);

                patient.save().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(person -> {
                            //do nothing
                        }, throwable -> Log.e(TAG, throwable.getMessage()));
            }
        });
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
            return new SaveSyncToken(this).findMyDeviceId();
        }
    }
}