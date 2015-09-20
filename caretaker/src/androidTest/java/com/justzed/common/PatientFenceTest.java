package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.caretaker.Application;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by freeman on 8/16/15.
 * tests covers CRD (no update) operations for Person
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientFenceTest extends ApplicationTestCase<Application> {
    private static final String TAG = PatientFenceTest.class.getName();

    private final String patientToken = "someyadayadahardcodedpatienttoken";


    private Person patient;
    private LatLng center;
    private double radius;
    private LatLng center1;
    private double radius1;
    private LatLng center2;
    private double radius2;

    public PatientFenceTest() {
        super(Application.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        //test creation
        patient = new Person(Person.PATIENT, patientToken)
                .save()
                .toBlocking()
                .single();

        assertNotNull(patient.getObjectId());

        center = new LatLng(0, 0);
        center1 = new LatLng(1, 0);
        center2 = new LatLng(2, 0);
        radius = 1.0f;
        radius1 = 1.0f;
        radius2 = 1.0f;


    }

    @Test
    public void testConstructer() {
        PatientFence fence = new PatientFence(patient, center, radius);
        assertEquals(fence.getPatient().getType(), Person.PATIENT);
        assertEquals(fence.getPatient().getUniqueToken(), patientToken);
        assertEquals(fence.getCenter(), center);
        assertEquals(fence.getRadius(), radius);
    }


    //create
    @Test
    public void testCreateDelete() {

        //test create
        PatientFence fence = new PatientFence(patient, center, radius)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());

        //test second
        PatientFence fence1 = new PatientFence(patient, center1, radius1)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence1.getObjectId());

        //test third
        PatientFence fence2 = new PatientFence(patient, center2, radius2)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence2.getObjectId());

        //test delete

        try {
            assertNull(fence.delete().toBlocking().single());
            assertNull(fence.getObjectId());
            assertNull(fence1.delete().toBlocking().single());
            assertNull(fence1.getObjectId());
            assertNull(fence2.delete().toBlocking().single());
            assertNull(fence2.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    //read
    @Test
    public void testRead() {

        //setUp
        PatientFence fence = new PatientFence(patient, center, radius)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());

        //test second
        PatientFence fence1 = new PatientFence(patient, center1, radius1)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence1.getObjectId());

        //test third
        PatientFence fence2 = new PatientFence(patient, center2, radius2)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence2.getObjectId());


        //read test
        List<PatientFence> patientFences = PatientFence.getPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences);
        assertEquals(patientFences.size(), 3);
        assertEquals(patientFences.get(0).getPatient().getObjectId(), patient.getObjectId());
        assertEquals(patientFences.get(0).getCenter(), center);
        assertEquals(patientFences.get(0).getRadius(), radius);


        //tearDown

        try {
            assertNull(fence.delete().toBlocking().single());
            assertNull(fence.getObjectId());
            assertNull(fence1.delete().toBlocking().single());
            assertNull(fence1.getObjectId());
            assertNull(fence2.delete().toBlocking().single());
            assertNull(fence2.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }


    //edit
    @Test
    public void testEdit() {

        //setUp
        PatientFence fence = new PatientFence(patient, center, radius)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());


        //read test
        List<PatientFence> patientFences = PatientFence.getPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences);
        assertEquals(patientFences.size(), 1);

        PatientFence fenceToEdit = patientFences.get(0);
        assertEquals(fenceToEdit.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(fenceToEdit.getCenter(), center);
        assertEquals(fenceToEdit.getRadius(), radius);

        fenceToEdit.setCenter(center1);
        fenceToEdit.setRadius(radius1);
        fenceToEdit.save().toBlocking().single();

        assertEquals(fenceToEdit.getObjectId(), fence.getObjectId());


        //re-read
        List<PatientFence> patientFences1 = PatientFence.getPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences1);
        assertEquals(patientFences1.size(), 1);

        PatientFence retrievedFence = patientFences.get(0);
        assertEquals(retrievedFence.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(retrievedFence.getCenter(), center1);
        assertEquals(retrievedFence.getRadius(), radius1);


        //tearDown

        try {
            assertNull(fence.delete().toBlocking().single());
            assertNull(fence.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @After
    protected void tearDown() throws Exception {
        try {
            assertNull(patient.delete().toBlocking().single());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        patient = null;

        super.tearDown();
    }
}
