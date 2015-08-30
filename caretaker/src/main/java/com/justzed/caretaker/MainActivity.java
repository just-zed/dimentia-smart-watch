package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.Person;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private Person person;

    public static final String PREF_PERSON_KEY = "PersonPref";


    @OnClick(R.id.button)
    void mapButtonClick(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //TODO: move these to a splash screen activity
        //first run check if patient is already created. if not create it
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!mPrefs.contains(PREF_PERSON_KEY)) {
            //create patient and save

            new Person(Person.CARETAKER, generateToken())
                    .save()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(person -> {
                        // save person in app
                        this.person = person;
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString(PREF_PERSON_KEY, person.getUniqueToken());
                        editor.apply();
                        finishActivityWithResult();
                    });


        } else {
            //get patient token from app data,
            // get person object from database and start service
            String uniqueToken = mPrefs.getString(PREF_PERSON_KEY, "");

            Person.getByUniqueToken(uniqueToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            person -> {
                                this.person = person;
                                finishActivityWithResult();
                            },
                            throwable -> {
                            }
                    );

        }

    }

    private void finishActivityWithResult() {
        // if activity is called by NfcActivity, close and return result
        if (getCallingActivity() != null) {
            Intent result = new Intent();
            result.putExtra(NfcActivity.INTENT_TOKEN_KEY, person.getUniqueToken());
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private String generateToken() {
        return new SaveSyncToken(this).findMyDeviceId();
    }

}