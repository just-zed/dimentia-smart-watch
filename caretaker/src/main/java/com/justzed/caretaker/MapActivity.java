package com.justzed.caretaker;

import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;


public class MapActivity extends FragmentActivity {
    private static final int UPDATE_TIMER_NORMAL = 5000;
    private static final int UPDATE_TIMER_PAUSE = 30000;

    private static final double[] BRISBANE_LAT_LONG = new double[]{-27.471010, 153.0333};


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        checkIfSetUpMapNeeded();
        countdownToNextUpdate(UPDATE_TIMER_NORMAL);
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
        countdownToNextUpdate(UPDATE_TIMER_PAUSE);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    /**
     * This checks if the map needs to be set up.
     */
    public void checkIfSetUpMapNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This method is used to set up the map.
     */
    private void setUpMap() {
        //retrieve location from other method
        //Set the patients location
        showPatientOnMap(BRISBANE_LAT_LONG[0], BRISBANE_LAT_LONG[1]);
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method adds a marker for the patient on the map.
     */
    public void showPatientOnMap(double patientCurrentLocationLat, double patientCurrentLocationLong) {
        patientMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(patientCurrentLocationLat, patientCurrentLocationLong)).title("TestPatient"));
        mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 1.0f));
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(final Marker marker, final LatLng toPosition,
                                           final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();


        //TODO: improve these to rx subscribe/observe pattern
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                //mMap.moveCamera(newLatLng(patientMarker.getPosition()));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

    }

    /**
     * Created by Tristan Duboi
     * <p>
     * Countdown till the next patient location update.
     */
    public void countdownToNextUpdate(int timeBetweenUpdates) {
        new CountDownTimer(timeBetweenUpdates, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                LatLng coordinates = new LatLng(0, 0);
                updatePatientLocationOnMap(patientMarker, coordinates, false);
            }
        }.start();
    }
}

