//package com.justzed.caretaker;
//
//import android.support.test.runner.AndroidJUnit4;
//import android.support.test.uiautomator.UiDevice;
//import android.support.test.uiautomator.UiObject;
//import android.support.test.uiautomator.UiObjectNotFoundException;
//import android.support.test.uiautomator.UiSelector;
//import android.test.InstrumentationTestCase;
//import android.test.suitebuilder.annotation.LargeTest;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//
///**
// * Created by Tristan on 14/08/2015.
// * <p>
// * This class is used to test the MapActivity Class.
// *
// * @author Tristan Dubois
// * @since 2015-08-14
// * <p>
// * TODO: map tests are not working, we'll fix it when we have time.
// */
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class MapActivityTest extends InstrumentationTestCase {
//    //Variables
//    public MapActivity mapActivity;
//    private static GoogleMap testMap; // Might be null if Google Play services APK is not available.
//    private Marker testPatientMarker;
//    UiDevice device;
//
//    //Constants
//    private LatLng START_LAT_LONG = new LatLng(0, 0);
//    private LatLng BASIC_LAT_LONG = new LatLng(-27.471010, 153.0333);
//    private LatLng NEGATIVE_LAT_LONG = new LatLng(-500, -500);
//    private LatLng LARGE_LAT_LONG = new LatLng(10000, 10000);
//
//    @Before
//    public void setUp() {
//        mapActivity = new MapActivity();
//        device = UiDevice.getInstance(getInstrumentation());
//
//    }
//
//    @Test
//    public void testCheckIfSetUpMapNeededNull() {
//        mapActivity.checkIfSetUpMapNeeded();
//    }
//
//    @Test
//    public void testCheckIfSetUpMapNeededNotNull() {
//        mapActivity.checkIfSetUpMapNeeded();
//
//    }
//
//    @Test
//    public void testUpdateatientLocationOnMapChangeLocationBasic() {
//
//        testPatientMarker = testMap.addMarker(new MarkerOptions().position(START_LAT_LONG).title("testPatient"));
//        mapActivity.updatePatientLocationOnMap(testPatientMarker, BASIC_LAT_LONG, false);
//    }
//
//    @Test
//    public void testUpdateatientLocationOnMapChangeLocationNegative() {
//        testPatientMarker = testMap.addMarker(new MarkerOptions().position(START_LAT_LONG).title("testPatient"));
//        mapActivity.updatePatientLocationOnMap(testPatientMarker, NEGATIVE_LAT_LONG, false);
//    }
//
//    @Test
//    public void testUpdateatientLocationOnMapChangeLocationSmall() {
//        testPatientMarker = testMap.addMarker(new MarkerOptions().position(START_LAT_LONG).title("testPatient"));
//        mapActivity.updatePatientLocationOnMap(testPatientMarker, LARGE_LAT_LONG, false);
//    }
//
//
//    @Test
//    public void testCenterPatientMarker() throws UiObjectNotFoundException {
//        mapActivity.checkIfSetUpMapNeeded();
//        UiObject marker = device.findObject(new UiSelector().descriptionContains("testPatient"));
//        marker.click();
//    }
//}

