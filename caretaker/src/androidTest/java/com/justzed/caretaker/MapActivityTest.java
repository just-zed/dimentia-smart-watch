package com.justzed.caretaker;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.test.ActivityTestCase;
import static org.mockito.Mockito.mock;


import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

/**
 * Created by Tristan on 14/08/2015.
 *
 * This class is used to test the MapActivity Class.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapActivityTest extends ActivityTestCase {
    //Variables
    public MapActivity mapActivity;
    private static GoogleMap testMap; // Might be null if Google Play services APK is not available.
    private Marker testPatientMarker;

    //Constants
    private double[] BASIC_LAT_LONG_BRISBANE = new double[]{-27.471010, 153.0333};
    private double[] NEGATIVE_LAT_LONG_BRISBANE = new double[]{-500, -500};
    private double[] LARGE_LAT_LONG_BRISBANE = new double[]{10000, 10000};
    private int NO_TIMER = 0;
    private int LARGE_TIMER = 10000;
    private int SMALL_TIMER = 10;

    @Before
    public void createMapActvity() {
        mapActivity = new MapActivity();
        mapActivity.checkIfSetUpMapNeeded();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void testCheckIfSetUpMapNeededNull() {
        mapActivity.checkIfSetUpMapNeeded();
    }

    @Test
    public void testCheckIfSetUpMapNeededNotNull() {
        mapActivity.checkIfSetUpMapNeeded();
        mapActivity.checkIfSetUpMapNeeded();
    }

    @Test
    public void testShowPatientOnMapBasicException() {
        mapActivity.showPatientOnMap(BASIC_LAT_LONG_BRISBANE[0], BASIC_LAT_LONG_BRISBANE[1]);
    }

    @Test
    public void testShowPatientOnNegativeException() {
        mapActivity.showPatientOnMap(NEGATIVE_LAT_LONG_BRISBANE[0], NEGATIVE_LAT_LONG_BRISBANE[1]);
    }

    @Test
    public void testShowPatientOnMapLargeException() {
        mapActivity.showPatientOnMap(LARGE_LAT_LONG_BRISBANE[0], LARGE_LAT_LONG_BRISBANE[1]);
    }


    @Test
    public void testUpdateatientLocationOnMapChangeLocation() {

        //mapActivity.updatePatientLocationOnMap(patientMarker, position, false);
    }

    @Test
    public void testcountdownToNextUpdateExceptionNoTimer() {
        mapActivity.countdownToNextUpdate(NO_TIMER);
    }

    @Test
    public void testcountdownToNextUpdateExceptionLargeTimer() {
        mapActivity.countdownToNextUpdate(LARGE_TIMER);
    }

    @Test
    public void testcountdownToNextUpdateExceptionSmallTimer() {
        mapActivity.countdownToNextUpdate(SMALL_TIMER);
    }
}

