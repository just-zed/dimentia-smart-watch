package com.justzed.patient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TODO: this doesn't seem to be working
 * Created by freeman on 8/23/15.
 */
public class PatientService extends IntentService {
    private static final String TAG = PatientService.class.getName();
    private Subscription subscription;
    GeofencingCheck geofenceCheck = new GeofencingCheck();

    private static final int INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 500;
    private static final int POLL_TIMER = 10000;



    public PatientService() {
        super(PatientService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private Observable<Location> getLocationUpdates() {
        return Observable.create(subscriber -> {
            try {
                LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // do that something
                        double[] locationToBeChecked= new double[]{location.getLatitude(), location.getLongitude()};

                        checkGeofenceStatus(locationToBeChecked);
                        subscriber.onNext(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String provider = locationManager.getBestProvider(criteria, true);

                locationManager.requestLocationUpdates(provider, INTERVAL, 0, locationListener);



            } catch (SecurityException e) {
                subscriber.onError(e);
            }
        });


    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // get device id

        Bundle data = intent.getExtras();
        final Person person = data.getParcelable(Person.PARCELABLE_KEY);
        getLocationUpdates()
                .filter(location1 -> location1 != null)
                .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                .flatMap(latLng -> new PatientLocation(person, latLng).save())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientLocation -> {
                    Log.e(TAG, "location updated: " + patientLocation.getObjectId());
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                });

//
//        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setNumUpdates(1)
//                .setInterval(INTERVAL)
//                .setFastestInterval(FASTEST_INTERVAL);
//
//        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplication());
//        subscription = locationProvider.getUpdatedLocation(request)
////                .delay(POLL_TIMER, TimeUnit.MILLISECONDS)
////                .repeat()
//                .map(location -> {
//                    return;
//                })
//                .flatMap(latLng -> {
//                    return;
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(patientLocation -> {
//                    Log.e(TAG, "save location success" + patientLocation.getObjectId());
//                }, throwable -> {
//                    Log.e(TAG, throwable.getMessage());
//                });

    }

    private void checkGeofenceStatus(double[] myLocation){
        final int geofenceStatus;
        final int EXITED_A_FENCE = 1;
        final int REENTERED_A_FENCE = 2;
        final int NOTHING_HAS_CHANGED = 0;
        final int NO_GEOFENCES_FOUND = 3;

        geofenceStatus = geofenceCheck.checkGeofence(myLocation);

        switch(geofenceStatus) {
            case NOTHING_HAS_CHANGED:
                //Nothing

                break;
            case NO_GEOFENCES_FOUND:
                //Nothing

                break;
            case EXITED_A_FENCE:
                //Exited a fence notification

                break;
            case REENTERED_A_FENCE:
                //The patient has re-entered a fence notification

                break;
        }


    }
}
