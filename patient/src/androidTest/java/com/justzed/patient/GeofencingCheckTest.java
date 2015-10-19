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
import java.util.Calendar;
import java.util.List;

/**
 * This class tests the geofencingCheck class.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-09-03
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

    private final int TIME_MODIFIER_NEGATIVE_HIGH = -10;
    private final int TIME_MODIFIER_NEGATIVE_LOW = -5;
    private final int TIME_MODIFIER_POSITIVE = 10;
    private final int TIME_MODIFIER_NEGATIVE = -10;

    /**
     * Sets up the tests.
     *
     * @return Nothing.
     */
    @Before
    public void setUp(){
        geofenceCheck = new GeofencingCheck();
        geofences = new ArrayList<PatientFence>();
        geofencesTwo = new ArrayList<PatientFence>();
    }

    /**
     * Test passes no exceptions are thrown when running checkGeofence with an arbitrary
     * location.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckGeofenceExceptionTest(){
        geofenceCheck.checkGeofence(createALocation(POSITION_ONE), patient);
    }

    /**
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with One item.
     *
     * @return Nothing.
     */
    @Test
    public void testGetGeofencesFromDatabaseOneItemExeption(){
        geofences.add(createAGeofence(GEOFENCE_ONE));

        geofencesTwo = geofenceCheck.getGeofencesFromDatabase(geofences);

        assertTrue(geofences == geofencesTwo);
    }

    /**
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with many items.
     *
     * @return Nothing.
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
     * Test passes no exceptions are thrown when running getGeofencesFromDatabase
     * with no items.
     *
     * @return Nothing.
     */
    @Test
    public void testGetGeofencesFromDatabaseNoItemsExeption(){
        geofencesTwo = geofenceCheck.getGeofencesFromDatabase(geofences);

        assertTrue(geofences == geofencesTwo);
    }

    /**
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with only one
     * geofence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with only one
     * geofence
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an Inside_FENCE with multiple
     * geofences
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with multiple
     * geofence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a huge geofence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE with a far away geofence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a small geofence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE with a geofence in the negative
     * lat long
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns the same result as it has before when
     * there is no geofences after being inside a fence.
     *
     * @return Nothing.
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
     * Test passes if the checkIfInsideGeofences returns the same result as it has before when
     * there is no geofences after being outside fence.
     *
     * @return Nothing.
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
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are INSIDE_FENCE.
     *
     * @return Nothing.
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
     * Test passes if checkIfStatusHasChanged returns NOTHING_HAS_CHANGED when both the current
     * and previous status are OUTSIDE_FENCE.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutside() {
        int currentStatus = GeofencingCheck.OUTSIDE_FENCE;
        int previousStatus = GeofencingCheck.OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus, previousStatus) ;
        assertTrue(result == GeofencingCheck.NOTHING_HAS_CHANGED);
    }

    /**
     * Test passes if checkIfStatusHasChanged returns EXITED_A_FENCE when both the current status is
     * OUTSIDE_FENCE and previous status is INSIDE_FENCE.
     *
     * @return Nothing.
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
     * Test passes if checkIfStatusHasChanged returns REENTERED_A_FENCE when both the current status is
     * INSIDE_FENCE and previous status is OUTSIDE_FENCE.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfStatusHasChangedBothOutsideToInside() {
        int currentStatus = GeofencingCheck.INSIDE_FENCE;
        int previousStatus = GeofencingCheck.OUTSIDE_FENCE;
        int result;

        result = geofenceCheck.checkIfStatusHasChanged(currentStatus,previousStatus) ;

        assertTrue(result == GeofencingCheck.REENTERED_A_FENCE);
    }


    /**
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE when inside a fence
     * with a timer that has not expired.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfInsideGeofenceWithTimerWithinTime(){
        int result;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        startTime.add(Calendar.MINUTE, TIME_MODIFIER_NEGATIVE);
        endTime.add(Calendar.MINUTE, TIME_MODIFIER_POSITIVE);

        geofences.add(createAGeofence(GEOFENCE_ONE, startTime, endTime));
        geofences.add(createAGeofence(GEOFENCE_FAR));

        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
    }

    /**
     * Test passes if the checkIfInsideGeofences returns an OUTSIDE_FENCE when inside a fence
     * with a timer that has expired.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfInsideGeofenceWithTimerOutsideTime(){
        int result;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        startTime.add(Calendar.MINUTE, TIME_MODIFIER_NEGATIVE_HIGH);
        endTime.add(Calendar.MINUTE, TIME_MODIFIER_NEGATIVE_LOW);

        geofences.add(createAGeofence(GEOFENCE_ONE, startTime, endTime));
        geofences.add(createAGeofence(GEOFENCE_FAR));

        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.OUTSIDE_FENCE);
    }

    /**
     * Test passes if the checkIfInsideGeofences returns an INSIDE_FENCE when inside a fence
     * with a timer that has expired but the start time is after the end time.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfInsideGeofenceWithTimerEndTimeBeforeStartTime(){
        int result;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        startTime.add(Calendar.MINUTE, TIME_MODIFIER_NEGATIVE_LOW);
        endTime.add(Calendar.MINUTE, TIME_MODIFIER_NEGATIVE);

        geofences.add(createAGeofence(GEOFENCE_ONE, startTime, endTime));
        geofences.add(createAGeofence(GEOFENCE_FAR));

        currentLocation = POSITION_ONE;

        result = geofenceCheck.checkIfInsideGeofences(geofences, createALocation(currentLocation));
        assertTrue(result == GeofencingCheck.INSIDE_FENCE);
    }

    /**
     * This method creates a new PatientFence object.
     *
     * @param geofenceData This contains the coordinates and radius for a geofence.
     * @param startTime the start time of a timer.
     * @param endTime the end time of a timer.
     * @return PatientFence This returns a PatientFence with the radius and coordinates of the geofenceData.
     */
    private PatientFence createAGeofence(double[] geofenceData, Calendar startTime, Calendar endTime){
        final int LATITUDE = 0;
        final int LONGITUDE = 1;
        final int CENTER = 2;
        PatientFence fence;

        fence = new PatientFence(patient, new LatLng(geofenceData[LATITUDE],geofenceData[LONGITUDE]), geofenceData[CENTER]);

        fence.setStartTime(startTime);
        fence.setEndTime(endTime);
        return fence;
    }

    /**
     * This method creates a new PatientFence object.
     *
     * @param geofenceData This contains the coordinates and radius for a geofence.
     * @return PatientFence This returns a PatientFence with the radius and coordinates of the geofenceData.
     */
    private PatientFence createAGeofence(double[] geofenceData){
        final int LATITUDE = 0;
        final int LONGITUDE = 1;
        final int CENTER = 2;

        return new PatientFence(patient, new LatLng(geofenceData[LATITUDE],geofenceData[LONGITUDE]), geofenceData[CENTER]);
    }

    /**
     * This method creates a new PatientLocation object.
     *
     * @param locationData This contains the coordinates for a location.
     * @return PatientFence This returns a PatientLocation with the coordinates of the locationData.
     */
    private PatientLocation createALocation(double[] locationData){
        final int LATITUDE = 0;
        final int LONGITUDE = 1;

        return new PatientLocation(patient, new LatLng(locationData[LATITUDE], locationData[LONGITUDE]));
    }
}

