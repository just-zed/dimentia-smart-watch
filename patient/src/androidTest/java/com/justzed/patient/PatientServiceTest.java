package com.justzed.patient;


import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * This class tests the PatientService class
 *
 * @author Tristan Dubois
 * @version 1.00
 * @since 2015-09-19
 */
public class PatientServiceTest extends TestCase {
    private GeofencingCheck geofenceCheck;
    private List<PatientFence> geofences = new ArrayList<PatientFence>();
    Person patient =  new Person(Person.PATIENT,"averyuniqueid");
    private PatientService patientService;

    private final double[] GEOFENCE = new double[]{27.471011,153.023449,3000};
    private final double[] POSITION = new double[]{27.471585,153.024713};

    /**
     * Sets up the tests for the PatientService class.
     *
     * @return Nothing.
     */
    @Before
    public void setUp(){
        patientService = new PatientService();
        geofenceCheck = new GeofencingCheck();

        geofences.add(createAGeofence(GEOFENCE));

    }

    /**
     * Fails if an exception is thrown when checkGeofenceStatus is thrown.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckGeofenceStatusException(/*PatientLocation myLocation, Person patient*/){
        patientService.checkGeofenceStatus(createALocation(POSITION),patient);
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
