package com.justzed.common.model;

import android.app.Application;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.TestSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by freeman on 8/16/15.
 * tests covers CRD (no update) operations for Person
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientLocationTest extends ApplicationTestCase<Application> {
    private static final String TAG = PatientLocationTest.class.getName();

    private String patientToken;


    private Person patient;
    private LatLng latLng;
    private LatLng latLng2;

    public PatientLocationTest() {
        super(Application.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        patientToken = "test_patient_" + Math.random() * 1000;

        latLng = new LatLng(0, 0);
        latLng2 = new LatLng(1, 1);


        try {
            TestSetup.setupParse(getContext());
        } catch (Exception e) {

        }
        patient = new Person(Person.PATIENT, patientToken)
                .save()
                .toBlocking()
                .single();

        assertNotNull(patient.getObjectId());

    }

    @Test
    public void testConstructer() {
        PatientLocation link = new PatientLocation(patient, latLng);
        assertEquals(link.getPatient().getType(), Person.PATIENT);
        assertEquals(link.getPatient().getUniqueToken(), patientToken);
        assertEquals(link.getLatLng(), latLng);

        assertNull(link.getParseObject());
    }

    //create
    @Test
    public void testCreateDelete() {

        //test create
        PatientLocation location = new PatientLocation(patient, latLng)
                .save()
                .toBlocking()
                .single();

        assertNotNull(location.getObjectId());

        //test second
        PatientLocation location2 = new PatientLocation(patient, latLng)
                .save()
                .toBlocking()
                .single();

        assertNotNull(location2.getObjectId());

        //test delete

        try {
            assertNull(location.delete().toBlocking().single());
            assertNull(location.getObjectId());
            assertNull(location2.delete().toBlocking().single());
            assertNull(location2.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    //read
    @Test
    public void testRead() {

        assertNull(PatientLocation.findLatestPatientLocation(patient).toBlocking().single());

        //setUp
        PatientLocation location = new PatientLocation(patient, latLng)
                .save()
                .toBlocking()
                .single();

        assertNotNull(location.getObjectId());


        //read test
        PatientLocation patientLocation = PatientLocation.findLatestPatientLocation(patient).toBlocking().single();

        assertNotNull(patientLocation);
        assertEquals(patientLocation.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(patientLocation.getLatLng(), latLng);


        //add second location
        PatientLocation location2 = new PatientLocation(patient, latLng2)
                .save()
                .toBlocking()
                .single();

        assertNotNull(location2.getObjectId());

        //read test
        PatientLocation patientLocation2 = PatientLocation.findLatestPatientLocation(patient).toBlocking().single();

        assertNotNull(patientLocation2);
        //same patient
        assertEquals(patientLocation2.getPatient().getObjectId(), patientLocation.getPatient().getObjectId());
        //different location
        assertNotSame(patientLocation2.getLatLng().latitude, patientLocation.getLatLng().latitude);


        //tearDown

        try {
            assertNull(location.delete().toBlocking().single());
            assertNull(location2.delete().toBlocking().single());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @After
    public void tearDown() throws Exception {
        assertNull(patient.delete().toBlocking().single());
        assertTrue(true);

        patient = null;

        super.tearDown();
    }
}
