package com.justzed.caretaker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;


public class MapActivity extends FragmentActivity {
    private static final String TAG = MapActivity.TAG;
    //Variables
    private Person person;
    private PatientLocation patientLocation;

    private String patientMarkerName = "";
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;
    boolean test = false;

    //Constants

    private static final int UPDATE_TIMER_NORMAL = 10000;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMap = null;
        setContentView(R.layout.activity_map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfSetUpMapNeeded();
        countdownToNextUpdate(UPDATE_TIMER_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patientMarker.remove();
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This checks if the map needs to be set up.
     */
    public void checkIfSetUpMapNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        try {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.

                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }


            // Check if we were successful in obtaining the map.
            if (mMap == null || patientMarker == null) {
                setUpMap();
            }
        } catch (Exception e) {
            toast("Map could not be loaded.");
        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This method is used to set up the map.
     */
    private void setUpMap() {
        /* TODO Retrieve and set to the current location of the patient.*/

        getPatientLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showPatientOnMap,
                        throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method adds a marker for the patient on the map.
     */
    private void showPatientOnMap(LatLng patientCurrentLocation) {
        try {
            if (patientMarker == null) {
                patientMarker = mMap.addMarker(new MarkerOptions().position(patientCurrentLocation).title(getPatientName()));
            } else {
                updatePatientLocationOnMap(patientMarker, patientCurrentLocation, false);
            }
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {
            toast("A Marker could not be placed.");
        }
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(final Marker marker, final LatLng toPosition,
                                           final boolean hideMarker) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long INTERPOLATION_DURATION = 500;
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / INTERPOLATION_DURATION);
//                double lng = t * toPosition.longitude + (1 - t)
//                        * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t)
//                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        marker.setVisible(false);
//                    } else {
//                        marker.setVisible(true);
//                    }
//                }
//            }
//        });

        if (hideMarker) {
            marker.setVisible(false);
        } else {
            marker.setVisible(true);
            marker.setPosition(new LatLng(toPosition.latitude, toPosition.longitude));
        }
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * Countdown till the next patient location update.
     */
    private void countdownToNextUpdate(long timeBetweenUpdates) {
        subscription = getPatientLocation()
                .delay(timeBetweenUpdates, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latLng -> {
//                    toast("Update");
                    updatePatientLocationOnMap(patientMarker, latLng, false);
                });

    }

    /**
     * Created by Tristan Dubois
     * <p>
     * His activates when the center button is pressed and centers the map on the patient.
     */
    public void centerPatientMarker(View view) {
        try {
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {

            toast("Failed to center the map on the user.");

        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This gets the current location of the patient from the database.
     */
    private Observable<LatLng> getPatientLocation() {
        return Person.getByUniqueToken("ffffffff-fcfb-6ccb-0033-c58700000000")
                .flatMap(PatientLocation::getLatestPatientLocation)
                .map(PatientLocation::getLatLng);
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This gets the name of the patient from the database.
     */
    private String getPatientName() {
        patientMarkerName = "Bob";
        return patientMarkerName;
    }

    private void toast(String toastMessage) {
        try {
            Toast.makeText(MapActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception toast) {
        }
    }
}