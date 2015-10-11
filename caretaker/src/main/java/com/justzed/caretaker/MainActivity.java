package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.justzed.common.DeviceUtils;
import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final String PREF_PERSON_KEY = "PersonPref";
    private Person caretaker;


    // temp token
    private String token;

    @Bind(android.R.id.list)
    ListView listView;

    List<Person> personList = new ArrayList<>();
    ArrayAdapter<Person> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (caretaker == null) {
            getCaretaker()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            person -> finishActivityWithResult(),
                            throwable -> Log.e(TAG, throwable.getMessage())
                    );
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // launch patient activity
            Intent intent = new Intent(this, PatientActivity.class);
            Person patient = personList.get(position);
            if (parent != null) {
                intent.putExtra(Person.PARCELABLE_KEY, patient);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        populatePatientList();
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

        // if activity is called by NfcActivity, close and return result
        String extraTag = getIntent().getStringExtra("TAG");
        if (getCallingActivity() != null
                && extraTag != null
                && extraTag.equals(NfcActivity.TAG)
                && caretaker != null) {
            Intent result = new Intent();
            result.putExtra(Person.PARCELABLE_KEY, caretaker);
            setResult(RESULT_OK, result);
            finish();
        } else {
            populatePatientList();
        }
    }

    private void populatePatientList() {
        if (caretaker != null && adapter != null) {
            // populate list of patients
            PatientLink.findAllByCaretaker(caretaker)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            patientLinks -> {
                                personList.clear();
                                for (int i = 0; i < patientLinks.size(); i++) {
                                    PatientLink link = patientLinks.get(i);
                                    personList.add(link.getPatient());
                                }
                                adapter.notifyDataSetChanged();

                            },
                            throwable -> Log.e(TAG, throwable.getMessage())
                    );
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
