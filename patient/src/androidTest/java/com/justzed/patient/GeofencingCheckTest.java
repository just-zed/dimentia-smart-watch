package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;

import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tristan.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GeofencingCheckTest extends TestCase {
    //Variables
    GeofencingCheck geofenceCheck;
    List<double[]> geofences = new ArrayList<double[]>();;
    double[] currentLocation;

    //Constants
    private int INSIDE_FENCE = 0;
    private int OUTSIDE_FENCE = 1;

    private int EXITED_A_FENCE = 1;
    private int REENTERED_A_FENCE = 2;
    private int NOTHING_HAS_CHANGED = 0;

    private double[] GEOFENCE_ONE = new double[]{1,2,3};
    private double[] GEOFENCE_TWO = new double[]{10,5,1};
    private double[] GEOFENCE_THREE = new double[]{41,23,2};
    private double[] GEOFENCE_FOUR = new double[]{56,21,5};
    private double[] GEOFENCE_HUGE = new double[]{1,1,100};
    private double[] GEOFENCE_FAR = new double[]{100,100,1};
    private double[] GEOFENCE_NEGATIVE = new double[]{-1,-2,3};
    private double[] GEOFENCE_SMALL = new double[]{0.0000415,0.0000540,0.000100};

    private double[] POSITION_ONE = new double[]{2,1};
    private double[] POSITION_TWO = new double[]{100,2};
    private double[] POSITION_NEGATIVE = new double[]{-3,-4};
    private double[] POSITION_SMALL = new double[]{0.0000415,0.0001535};


    @Before
    public void setUp(){
        geofenceCheck = new GeofencingCheck();
        geofences = new ArrayList<double[]>();

    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running checkGeofence with an arbitrary
     * location.
     */
    @Test
    public void testCheckGeofenceExceptionTest(){
        geofenceCheck.checkGeofence(POSITION_ONE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase.
     */
    @Test
    public void testGetGeofencesFromDatabaseExeption(){
        geofenceCheck.getGeofencesFromDatabase();
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with only one
     * geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesSingleGeofenceInside(){
        int result;
        geofences.add(GEOFENCE_ONE);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with only one
     * geofence
     */
    @Test
    public void testCheckIfInsideGeofencesSingleGeofenceOutside(){
        int result;
        geofences.add(GEOFENCE_ONE);
        currentLocation = POSITION_TWO;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an Inside_FENCE with multiple
     * geofences
     */
    @Test
    public void testCheckIfInsideGeofencesMultipleGeofenceInside(){
        int result;
        geofences.add(GEOFENCE_TWO);
        geofences.add(GEOFENCE_THREE);
        geofences.add(GEOFENCE_ONE);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with multiple
     * geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesMultipleGeofenceOutside(){
        int result;
        geofences.add(GEOFENCE_FOUR);
        geofences.add(GEOFENCE_TWO);
        geofences.add(GEOFENCE_THREE);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a huge geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesHugeGeofence(){
        int result;
        geofences.add(GEOFENCE_HUGE);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with a far away geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesFarGeofence(){
        int result;
        geofences.add(GEOFENCE_FAR);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a small geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesSmallGeofence(){
        int result;
        geofences.add(GEOFENCE_SMALL);
        currentLocation = POSITION_SMALL;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a geofence in the negative
     * lat long
     */
    @Test
    public void testCheckIfInsideGeofencesNegativeGeofence(){
        int result;
        geofences.add(GEOFENCE_NEGATIVE);
        currentLocation = POSITION_NEGATIVE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns the same result as it has before when
     * there is no geofences after being inside a fence.
     */
    @Test
    public void testCheckIfInsideGeofencesNoGeofencesInside(){
        int result;
        geofences.add(GEOFENCE_ONE);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        geofences = new ArrayList<double[]>();
        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns the same result as it has before when
     * there is no geofences after being outside fence.
     */
    @Test
    public void testCheckIfInsideGeofencesNoGeofencesOutside(){
        int result;
        geofences.add(GEOFENCE_TWO);
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        geofences = new ArrayList<>();
        result = geofenceCheck.checkIfInsideGeofences(geofences, currentLocation);
        assertTrue(result == OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are INSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothInside() {
        int currentStatus = INSIDE_FENCE;
        int previousStatus = INSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus, previousStatus) ;
        assertTrue(result == NOTHING_HAS_CHANGED);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are OUTSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutside() {
        int currentStatus = OUTSIDE_FENCE;
        int previousStatus = OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;
        assertTrue(result == NOTHING_HAS_CHANGED);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns EXITED_A_FENCE when both the current status is
     * OUTSIDE_FENCE and previous status is INSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothInsideToOutside() {
        int currentStatus = OUTSIDE_FENCE;
        int previousStatus = INSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;
        assertTrue(result == EXITED_A_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns REENTERED_A_FENCE when both the current status is
     * INSIDE_FENCE and previous status is OUTSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutsideToInside() {
        int currentStatus = INSIDE_FENCE;
        int previousStatus = OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;

        assertTrue(result == REENTERED_A_FENCE);
    }
}
