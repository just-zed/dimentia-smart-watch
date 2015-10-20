package com.justzed.caretaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.justzed.common.DeviceUtils;
import com.justzed.common.SaveSyncToken;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;
import com.parse.ParsePush;

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

    List<Person> patientList = new ArrayList<>();
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
                            throwable -> {
                                Log.e(TAG, throwable.getMessage());
                                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.remove(PREF_PERSON_KEY);
                                editor.apply();
                                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                                finish();
                            });
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
        listView.setAdapter(adapter);

        /**
         * enter patient activity on short click
         */
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // launch patient activity
            Intent intent = new Intent(this, PatientActivity.class);
            Person patient = patientList.get(position);
            if (parent != null) {
                intent.putExtra(Person.PARCELABLE_KEY, patient);
                startActivity(intent);
            }
        });

        /**
         * edit patient name on long click
         */
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Person patient = patientList.get(position);
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View promptView = layoutInflater.inflate(R.layout.dialog_fragment_edittext, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(promptView).setTitle(R.string.edit_patient_name);

            final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
            editText.setText(patient.getName());
            AlertDialog alertDialog = alertDialogBuilder.setCancelable(false)
                    .setPositiveButton(R.string.save, null)
                    .setNeutralButton(R.string.delete, null)
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, id1) -> dialog.cancel())
                    .create();

            alertDialog.setOnDismissListener(dialog -> {
                populatePatientList();
            });
            alertDialog.setOnShowListener(dialog -> {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                positiveButton.setOnClickListener(v -> {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setError(getText(R.string.hint_enter_name));
                    } else {
                        // save text
                        patient.setName(editText.getText().toString());
                        patient.save().subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        person -> dialog.dismiss(),
                                        throwable -> Log.e(TAG, throwable.getMessage())
                                );
                    }
                });
                neutralButton.setOnClickListener(v ->
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(R.string.R_string_delete_confirm)
                                .setPositiveButton(R.string.delete, (dialog1, which) -> {
                                    PatientLink.findByPersons(patient, caretaker)
                                            .flatMap(PatientLink::delete)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    patientLink -> dialog.dismiss(),
                                                    throwable -> Log.e(TAG, throwable.getMessage())
                                            );
                                })
                                .setCancelable(true)
                                .setNegativeButton(R.string.cancel, (dialog2, which1) -> dialog2.cancel())
                                .show());
            });

            alertDialog.show();
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        populatePatientList();
    }


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

                        if (person1 == null) {
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.remove(PREF_PERSON_KEY);
                            editor.apply();
                            finish();
                        }
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


    /**
     * find list of patient by caretaker and populate list
     */
    private void populatePatientList() {
        if (caretaker != null && adapter != null) {
            // populate list of patients
            PatientLink.findAllByCaretaker(caretaker)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            patientLinks -> {
                                patientList.clear();
                                for (int i = 0; i < patientLinks.size(); i++) {
                                    PatientLink link = patientLinks.get(i);
                                    Person patient = link.getPatient();
                                    patientList.add(patient);
                                    ParsePush.subscribeInBackground("patient-" + patient.getUniqueToken());
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
