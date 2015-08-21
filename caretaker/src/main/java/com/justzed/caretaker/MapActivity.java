package com.justzed.caretaker;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InterruptedIOException;
import java.util.Random;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;


public class MapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;
    final Handler mapHandler = new Handler();
    private boolean randomValue = true;

    private final double[] BRISBANE_LAT_LONG = new double[]{-27.471010,153.0333};
    private final double[] NOT_BRISBANE_LAT_LONG = new double[]{-28.4667,154.0333};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        runThread();
    }

    private void runThread(){
        new Thread(){
            public void run(){
                    try {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                tempTestUpdate();
                            }
                        });
                        Thread.sleep((3000));
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();

                    }
                }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void tempTestUpdate() {

        if(randomValue) {
            updatePatientLocationOnMap(BRISBANE_LAT_LONG[0] , BRISBANE_LAT_LONG[1]);
            randomValue = false;
        }
        else{
            updatePatientLocationOnMap(NOT_BRISBANE_LAT_LONG[0] , NOT_BRISBANE_LAT_LONG[1]);
            randomValue = true;
        }
    }

    /**
     * Created by Tristan Dubois
     *
     * This checks if the map needs to be set up.
     */
    private void setUpMapIfNeeded() {
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
        patientMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(patientCurrentLocationLat, patientCurrentLocationLong)).title("TestPatient"));
        mMap.moveCamera(newLatLng(patientMarker.getPosition()));
    }

    /**
     * Created by Tristan Duboi
     *
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(double patientCurrentLocationLong, double patientCurrentLocationLat){
        patientMarker.setPosition(new LatLng(patientCurrentLocationLat, patientCurrentLocationLong));
    }


}

