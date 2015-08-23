package com.justzed.caretaker;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;

import android.test.suitebuilder.annotation.LargeTest;
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
public class MapActivityTest extends ActivityTestCase{
    //Variables
    public MapActivity mapActivity = new MapActivity();

    //Constants
    private double[] BASIC_LAT_LONG_BRISBANE = new double[]{-27.471010,153.0333};
    private double[] NEGATIVE_LAT_LONG_BRISBANE = new double[]{-500,-500};
    private double[] LARGE_LAT_LONG_BRISBANE = new double[]{10000,10000};

    @Before
    public void createMapActvity()
    {
        mapActivity = mock(MapActivity.class);


    }

    // checkIfSetUpMapNeeded
    @Test
    public void testCheckIfSetUpMapNeededNull(){
        mapActivity.checkIfSetUpMapNeeded();
    }

    @Test
    public void testCheckIfSetUpMapNeededNotNull(){
        mapActivity.checkIfSetUpMapNeeded();
        mapActivity.checkIfSetUpMapNeeded();
    }

    @Test
    public void testShowPatientOnMapBasicException(){
        mapActivity.showPatientOnMap(BASIC_LAT_LONG_BRISBANE[0], BASIC_LAT_LONG_BRISBANE[1]);
    }
    @Test
    public void testShowPatientOnNegativeException(){
        mapActivity.showPatientOnMap(NEGATIVE_LAT_LONG_BRISBANE[0],NEGATIVE_LAT_LONG_BRISBANE[1]);
    }

    @Test
    public void testShowPatientOnMapLargeException(){
        mapActivity.showPatientOnMap(LARGE_LAT_LONG_BRISBANE[0],LARGE_LAT_LONG_BRISBANE[1]);
    }


    //updatePatientLocationOnMap
    @Test
    public void testUpdateatientLocationOnMapChangeLocation(){

        // mapActivity.updatePatientLocationOnMap();
    }

    //Nothing
    //int countdownToNextUpdate (Timer)
        //exception test

        //change location
        //check with large location
        //check with small location
        //exception
}
