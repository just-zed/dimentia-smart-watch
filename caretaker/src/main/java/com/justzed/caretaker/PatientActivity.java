package com.justzed.caretaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import com.justzed.common.model.Person;
import com.parse.ParsePush;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientActivity extends Activity {

    private static final String TAG = PatientActivity.class.getSimpleName();
    private Person patient;


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

        setContentView(R.layout.activity_patient);

        ButterKnife.bind(this);
        button.setEnabled(false);
        buttonMessenger.setEnabled(false);
        switchPatientDisableCheck.setEnabled(false);

        Bundle data = getIntent().getExtras();
        patient = data.getParcelable(Person.PARCELABLE_KEY);

        initWithPatient();

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


    private void initWithPatient() {
        if (patient != null) {
            button.setEnabled(true);
            buttonMessenger.setEnabled(true);
            switchPatientDisableCheck.setEnabled(true);

            button.setText(String.format(getString(R.string.find_my_patient_button_text), patient.getName()));
            buttonMessenger.setText(String.format(getString(R.string.message_my_patient_button_text), patient.getName()));
            switchPatientDisableCheck.setText(String.format(getString(R.string.patient_nearby_text), patient.getName()));
            switchPatientDisableCheck.setChecked(patient.getDisableGeofenceChecks());
            switchPatientDisableCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                patient.setDisableGeofenceChecks(isChecked);

                patient.save().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(person -> {
                            //do nothing
                        }, throwable -> Log.e(TAG, throwable.getMessage()));
            });
        }


    }

}