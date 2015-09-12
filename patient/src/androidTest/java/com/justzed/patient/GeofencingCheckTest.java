package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;

import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

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
    List<PatientFence> geofences = new ArrayList<PatientFence>();
    List<PatientFence> geofencesTwo = new ArrayList<PatientFence>();
    double[] currentLocation;
    Person patient =  new Person(Person.PATIENT,"averyuniqueid");

    //Constants
    private final double[] GEOFENCE_ONE = new double[]{27.471011,153.023449,3000};
    private final double[] GEOFENCE_TWO = new double[]{10,5,1000};
    private final double[] GEOFENCE_THREE = new double[]{41,23,2000};
    private final double[] GEOFENCE_FOUR = new double[]{56,21,5000};
    private final double[] GEOFENCE_HUGE = new double[]{27.471011,153.023449,10000000};
    private final double[] GEOFENCE_FAR = new double[]{100,100,1000};
    private final double[] GEOFENCE_NEGATIVE = new double[]{-27.471011,-153.023449,3000};
    private final double[] GEOFENCE_SMALL = new double[]{0.0000415,0.0000540,1000};

    private final double[] POSITION_ONE = new double[]{27.471585,153.024713};
    private final double[] POSITION_TWO = new double[]{100,2};
    private final double[] POSITION_NEGATIVE = new double[]{-27.471011,-153.023449};
    private final double[] POSITION_SMALL = new double[]{0.0000415,0.0001535};


    @Before
    public void setUp(){
        geofenceCheck = new GeofencingCheck();
        geofences = new ArrayList<PatientFence>();
        geofencesTwo = new ArrayList<PatientFence>();
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running checkGeofence with an arbitrary
     * location.
     */
    @Test
    public void testCheckGeofenceExceptionTest(){
        geofenceCheck.checkGeofence(createALocation(POSITION_ONE), patient);
    }
    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with One item.
     */
    @Test
    public void testGetGeofencesFromDatabaseOneItemExeption(){
        geofences.add(createAGeofence(GEOFENCE_ONE));

        geofencesTwo = geofenceCheck.getGeofencesFromDatabase(geofences);

        assertTrue(geofences == geofencesTwo);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with many items.
     */
    @Test
    public void testGetGeofencesFromDatabaseManyItemsExeption(){
        geofences.add(createAGeofence(GEOFENCE_ONE));
        geofences.add(createAGeofence(GEOFENCE_TWO));
        geofences.add(createAGeofence(GEOFENCE_FOUR));

        geofencesTwo = geofenceCheck.getGeofencesFromDatabase(geofences);

        assertTrue(geofences == geofencesTwo);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with no items.
     */
    @Test
    public void testGetGeofencesFromDatabaseNoItemsExeption(){
        geofencesTwo = geofenceCheck.getGeofencesFromDatabase(geofences);

        assertTrue(geofences == geofencesTwo);
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

        geofences.add(createAGeofence(GEOFENCE_ONE));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_ONE));
        currentLocation = POSITION_TWO;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.OUTSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_TWO));
        geofences.add(createAGeofence(GEOFENCE_THREE));
        geofences.add(createAGeofence(GEOFENCE_ONE));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_FOUR));
        geofences.add(createAGeofence(GEOFENCE_TWO));
        geofences.add(createAGeofence(GEOFENCE_THREE));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a huge geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesHugeGeofence(){
        int result;
        geofences.add(createAGeofence(GEOFENCE_HUGE));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with a far away geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesFarGeofence(){
        int result;
        geofences.add(createAGeofence(GEOFENCE_FAR));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a small geofence.
     */
    @Test
    public void testCheckIfInsideGeofencesSmallGeofence(){
        int result;
        geofences.add(createAGeofence(GEOFENCE_SMALL));
        currentLocation = POSITION_SMALL;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_NEGATIVE));
        currentLocation = POSITION_NEGATIVE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_ONE));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));

        geofences = new ArrayList<>();

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
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
        geofences.add(createAGeofence(GEOFENCE_TWO));
        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        geofences = new ArrayList<>();
        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.OUTSIDE_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are INSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothInside() {
        int currentStatus = GeofencingCheck.INSIDE_FENCE;
        int previousStatus = GeofencingCheck.INSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus, previousStatus) ;
        assertTrue(result == GeofencingCheck.NOTHING_HAS_CHANGED);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are OUTSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutside() {
        int currentStatus = GeofencingCheck.OUTSIDE_FENCE;
        int previousStatus = GeofencingCheck.OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;
        assertTrue(result == GeofencingCheck.NOTHING_HAS_CHANGED);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns EXITED_A_FENCE when both the current status is
     * OUTSIDE_FENCE and previous status is INSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothInsideToOutside() {
        int currentStatus = GeofencingCheck.OUTSIDE_FENCE;
        int previousStatus = GeofencingCheck.INSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;
        assertTrue(result == GeofencingCheck.EXITED_A_FENCE);
    }

    /**
     * Created by Tristan Dubois.
     *
     * Test passes if checkIfStatusHasChanged returns REENTERED_A_FENCE when both the current status is
     * INSIDE_FENCE and previous status is OUTSIDE_FENCE.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutsideToInside() {
        int currentStatus = GeofencingCheck.INSIDE_FENCE;
        int previousStatus = GeofencingCheck.OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;

        assertTrue(result == GeofencingCheck.REENTERED_A_FENCE);
    }

    private PatientFence createAGeofence(double[] geofenceData){
        final int LATITUDE = 0;
        final int LONGITUDE = 1;
        final int CENTER = 2;

        return new PatientFence(patient, new LatLng(geofenceData[LATITUDE],geofenceData[LONGITUDE]), geofenceData[CENTER]);
    }

    private PatientLocation createALocation(double[] locationData){
        final int LATITUDE = 0;
        final int LONGITUDE = 1;

        return new PatientLocation(patient, new LatLng(locationData[LATITUDE], locationData[LONGITUDE]));
    }
}

