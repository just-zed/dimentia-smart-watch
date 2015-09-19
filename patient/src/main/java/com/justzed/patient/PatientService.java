package com.justzed.patient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TODO: this doesn't seem to be working
 * Created by freeman on 8/23/15.
 */
public class PatientService extends IntentService {
    private static final String TAG = PatientService.class.getName();
    GeofencingCheck geofenceCheck = new GeofencingCheck();

    private static final int INTERVAL = 5000;

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
        Observable.combineLatest(
                // get location updates observable
                getLocationUpdates()
                        .filter(location1 -> location1 != null)
                        .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                        .flatMap(latLng -> new PatientLocation(person, latLng).save()),
                // save geofence into geofenceCheck object
                PatientFence.getPatientFences(person), (patientLocation, patientFences) -> {
                    geofenceCheck.getGeofencesFromDatabase(patientFences);
                    // pass on patientLocation
                    return patientLocation;
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientLocation -> {
                    checkGeofenceStatus(
                            patientLocation, patientLocation.getPatient());
                });


    }

    /**
     * This method runs all the methods needed to check whether the device's status has changed.
     * If the patient leaves all geofences, a notification is sent to the other device once.
     * If the device re-enters the geofences, a notification is sent to the other device once.
     * If the caretaker has turned off geofence checks, no checks will be made.
     *
     * @param myLocation This is the location of a patient.
     * @param patient This is the Person database details of the patient.
     * @return Nothing.
     */
    public void checkGeofenceStatus(PatientLocation myLocation, Person patient){
        if (/**TODO change this to the correct get method*//*patient.getDisableGeofenceChecks() == */false) {

            @GeofencingCheck.StatusChange
            final int geofenceStatus = geofenceCheck.checkGeofence(myLocation, patient);

            String channelName = "patient-" + patient.getUniqueToken();

            switch (geofenceStatus) {
                case GeofencingCheck.NOTHING_HAS_CHANGED:
                    //Nothing
                    break;
                case GeofencingCheck.NO_GEOFENCES_FOUND:
                    //Nothing
                    break;
                case GeofencingCheck.EXITED_A_FENCE:
                    //Exited a fence notification
                    NotificationMessage.sendMessage(channelName, getString(R.string.exited_fence_notificiation));
                    break;
                case GeofencingCheck.REENTERED_A_FENCE:
                    //The patient has re-entered a fence notification
                    NotificationMessage.sendMessage(channelName, getString(R.string.reentered_fence_notificiation));
                    break;
            }
        }
        else{
            geofenceCheck.setPreviouslyInAFence(GeofencingCheck.INSIDE_FENCE);
        }
    }
}
