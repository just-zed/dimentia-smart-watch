package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

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


    @Bind(R.id.button)
    View button;

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

        //TODO: move these to a splash screen activity?
        getCaretaker()
                .flatMap(PatientLink::getByCaretaker)
                .map(patientLink -> {
                    this.patient = patientLink.getPatient();
                    return patientLink;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientLink -> finishActivityWithResult(),
                        throwable -> Log.e(TAG, throwable.getMessage()));


    }

    @NonNull
    private Observable<Person> getCaretaker() {
        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!mPrefs.contains(PREF_PERSON_KEY)) {
            //create patient and save

            return new Person(Person.CARETAKER, generateToken())
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
        // if activity is called by NfcActivity, close and return result
        if (getCallingActivity() != null && caretaker != null) {
            Intent result = new Intent();
            result.putExtra(Person.PARCELABLE_KEY, caretaker);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private String generateToken() {
        return "ffffffff-fcfb-6ccb-0033-c58700000000";
        //new SaveSyncToken(this).findMyDeviceId();
    }

}