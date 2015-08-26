package com.justzed.caretaker;

import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;


public class MapActivity extends FragmentActivity {
    private final int UPDATE_TIMER_NORMAL= 5000;
    private final int UPDATE_TIMER_PAUSE= 30000;

    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;
    private final double[] BRISBANE_LAT_LONG = new double[]{-27.471010,153.0333};

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
    protected void onPause(){
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

        try {
            if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }


            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        catch(Exception e){
            try
            {
                Toast.makeText(MapActivity.this, "Map could not be loaded", Toast.LENGTH_LONG).show();
            }catch(Exception Toast){}
        }
    }

    /**
     * Created by Tristan Dubois
     *
     * This method is used to set up the map.
     */
    private void setUpMap() {
        //retrieve location from other method
        //Set the patients location
        showPatientOnMap(BRISBANE_LAT_LONG[0],BRISBANE_LAT_LONG[1]);
    }

    /**
     * Created by Tristan Duboi
     *
     * This method adds a marker for the patient on the map.
     */
    public void showPatientOnMap( double patientCurrentLocationLat, double patientCurrentLocationLong) {
        try {
            patientMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(patientCurrentLocationLat, patientCurrentLocationLong)).title("TestPatient"));
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 1.0f));
        } catch (Exception e) {
            try {
                Toast.makeText(MapActivity.this, "A Marker could not be placed", Toast.LENGTH_LONG).show();
            } catch (Exception Toast) {
            }
        }
    }

    /**
     * Created by Tristan Duboi
     *
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(final Marker marker, final LatLng toPosition,
                                           final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long INTERPOLATION_DURATION = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / INTERPOLATION_DURATION);
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
     *
     * Countdown till the next patient location update.
     */
    public void countdownToNextUpdate(int timeBetweenUpdates){
        new CountDownTimer(timeBetweenUpdates, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                LatLng coordinates = new LatLng(0,0);
                updatePatientLocationOnMap(patientMarker,coordinates,false );
            }
        }.start();
    }
}

