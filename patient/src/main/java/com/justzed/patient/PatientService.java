package com.justzed.patient;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by freeman on 8/23/15.
 */
public class PatientService extends IntentService {
    private static final String TAG = PatientService.class.getName();
    private Subscription subscription;

    public static final String PREF_PATIENT_KEY = "patientPref";
    public static final String INTENT_PATIENT_KEY = "patientIntent";


    public PatientService() {
        super(PatientService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // get device id

        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(500);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplication());
        subscription = locationProvider.getUpdatedLocation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    Log.e(TAG, "update location");
                });


    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
        super.onDestroy();

    }
}
