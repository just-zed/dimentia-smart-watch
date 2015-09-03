package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.runner.RunWith;

/**
 * Created by Tristan.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GeofencingCheckTest {

    /**
     * Created by Tristan.
     *
     * Main method to check if a patient is in a geofence.
     */
        //Test for exceptions


     /**
     * Created by Tristan.
     *
     * This method gets the values of all geofences from the database and stores them in a list.
     */
        //Test for exception
        //Test with 1 Geofence
        //Test with no Geofences
        //Test with multiple Geofences


    /**
     * Created by Tristan.
     *
     * Get my current location and store it
     */
        //Test for exception

    /**
     * Created by Tristan.
     *
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     */
        //Test with 1 geofence (inside)
        //Test with multiple geofences(inside)
        //Test with no geofences(inside)
        //Test with 1 geofence(outside)
        //Test with multiple geofences(outside)
        //Test with no geofences(outside)


    /**
     * Created by Tristan.
     *
     * This checks if the patient has entered or exited a fence.
     */
        //check both inside
        //check both outside
        //check outside >> inside
        //check inside >> outside




}
