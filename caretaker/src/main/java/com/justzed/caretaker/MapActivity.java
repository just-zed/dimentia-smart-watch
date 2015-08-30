package com.justzed.caretaker;

import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
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
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;


public class MapActivity extends FragmentActivity {
    //Variables
    private Person person;
    private PatientLocation patientLocation;

    private String patientMarkerName = "";
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;
    boolean test = false;

    //Constants

    private final int UPDATE_TIMER_NORMAL= 10000;
    private final int UPDATE_TIMER_PAUSE= 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMap = null;
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
        patientMarker.remove();
    }

    /**
     * Created by Tristan Dubois
     *
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
        }
        catch(Exception e){
            toast("Map could not be loaded.");
        }
    }

    /**
     * Created by Tristan Dubois
     *
     * This method is used to set up the map.
     */
    private void setUpMap() {
        /* TODO Retrieve and set to the current location of the patient.*/
        showPatientOnMap(getPatientLocation());
    }

    /**
     * Created by Tristan Duboi
     *
     * This method adds a marker for the patient on the map.
     */
    private void showPatientOnMap( LatLng patientCurrentLocation) {
        try {
            patientMarker = mMap.addMarker(new MarkerOptions().position(patientCurrentLocation).title(getPatientName()));
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {
            toast("A Marker could not be placed.");
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
        final long INTERPOLATION_DURATION = 500;
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
    private void countdownToNextUpdate(int timeBetweenUpdates){
        new CountDownTimer(timeBetweenUpdates, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                toast("Update");
                //LatLng coordinates = getPatientLocation();
                //updatePatientLocationOnMap(patientMarker,coordinates,false );
            }
        }.start();
    }

    /**
     * Created by Tristan Dubois
     *
     * His activates when the center button is pressed and centers the map on the patient.
     */
    public void centerPatientMarker(View view){
        try {
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {

            toast("Failed to center the map on the user.");

        }
    }

    /**
     * Created by Tristan Dubois
     *
     * This gets the current location of the patient from the database.
     */
    private LatLng getPatientLocation(){
        //patientLocation = PatientLocation.getLatestPatientLocation(person).toBlocking().single();
        //return patientLocation.getLatLng();

        return new LatLng(0,0);

    }

    /**
     * Created by Tristan Dubois
     *
     * This gets the name of the patient from the database.
     */
    private String getPatientName(){
        patientMarkerName = "Bob";
        return patientMarkerName;
    }

    private void toast(String toastMessage) {
        try{
            Toast.makeText(MapActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        }catch(Exception toast){}
    }
}