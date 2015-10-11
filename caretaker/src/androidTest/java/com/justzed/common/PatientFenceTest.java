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

import java.util.Calendar;
import java.util.List;

/**
 * Tests covers CRUD operations for PatientFence
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-09-05
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientFenceTest extends ApplicationTestCase<Application> {
    private static final String TAG = PatientFenceTest.class.getName();

    private String patientToken;


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

    /**
     * Sets up the tests.
     *
     * @return Nothing.
     */
    @Before
    protected void setUp() throws Exception {
        super.setUp();

        patientToken = "test_patient_" + Math.random() * 1000;
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

    /**
     * Tests the constructors
     *
     * @return Nothing.
     */
    @Test
    public void testConstructer() {
        PatientFence fence = new PatientFence(patient, center, radius);
        assertEquals(fence.getPatient().getType(), Person.PATIENT);
        assertEquals(fence.getPatient().getUniqueToken(), patientToken);
        assertEquals(fence.getCenter(), center);
        assertEquals(fence.getRadius(), radius);
    }

    /**
     * Tests the create and delete methods
     *
     * @return Nothing.
     */
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

    /**
     * Tests the read methods
     *
     * @return Nothing.
     */
    //read
    @Test
    public void testRead() {

        //setUp
        PatientFence fence = new PatientFence(patient, center, radius);
        fence.setGroupId(1);

        fence.save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());

        //test second
        PatientFence fence1 = new PatientFence(patient, center1, radius1);
        fence1.setGroupId(3);

        fence1.save()
                .toBlocking()
                .single();

        assertNotNull(fence1.getObjectId());

        //test third
        PatientFence fence2 = new PatientFence(patient, center2, radius2);
        fence2.setGroupId(2);
        fence2.save()
                .toBlocking()
                .single();

        assertNotNull(fence2.getObjectId());


        //read test
        List<PatientFence> patientFences = PatientFence.findPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences);
        assertEquals(patientFences.size(), 3);
        // first one should be at last
        assertEquals(patientFences.get(2).getPatient().getObjectId(), patient.getObjectId());
        assertEquals(patientFences.get(2).getCenter(), center);
        assertEquals(patientFences.get(2).getRadius(), radius);

        Long maxGroupId = PatientFence.findMaxGroupId(patient).toBlocking().single();

        assertTrue(maxGroupId == 3);


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

    private static final String TIME_STRING_FORMATTER = "%tc";

    /**
     * Tests the edit methods
     *
     * @return Nothing.
     */
    //edit
    @Test
    public void testEdit() {

        List<PatientFence> existingList = PatientFence.findPatientFences(patient).toBlocking().single();

        int existingSize = existingList != null ? existingList.size() : 0;
        //setUp
        PatientFence fence = new PatientFence(patient, center, radius)
                .save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());


        //read test
        List<PatientFence> patientFences = PatientFence.findPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences);
        assertEquals(patientFences.size(), existingSize + 1);

        PatientFence fenceToEdit = patientFences.get(0);
        assertEquals(fenceToEdit.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(fenceToEdit.getCenter(), center);
        assertEquals(fenceToEdit.getRadius(), radius);

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.HOUR, 3);

        String startTimeString = String.format(TIME_STRING_FORMATTER, startTime);
        String endTimeString = String.format(TIME_STRING_FORMATTER, endTime);


        fenceToEdit.setCenter(center1);
        fenceToEdit.setRadius(radius1);
        fenceToEdit.setStartTime(startTime);
        fenceToEdit.setEndTime(endTime);
        fenceToEdit.setGroupId(3);
        fenceToEdit.save().toBlocking().single();

        assertEquals(fenceToEdit.getObjectId(), fence.getObjectId());

        //re-read
        List<PatientFence> patientFences1 = PatientFence.findPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences1);
        assertEquals(patientFences1.size(), 1);

        PatientFence retrievedFence = patientFences.get(0);
        assertEquals(retrievedFence.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(retrievedFence.getCenter(), center1);
        assertEquals(retrievedFence.getRadius(), radius1);

        assertEquals(String.format(TIME_STRING_FORMATTER, retrievedFence.getStartTime()), startTimeString);
        assertEquals(String.format(TIME_STRING_FORMATTER, retrievedFence.getEndTime()), endTimeString);
        assertEquals(retrievedFence.getGroupId(), 3);


        //tearDown

        try {
            assertNull(retrievedFence.delete().toBlocking().single());
            assertNull(retrievedFence.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testComplexRead() {
        //setUp
        PatientFence fence = new PatientFence(patient, center, radius);

        fence.save()
                .toBlocking()
                .single();

        assertNotNull(fence.getObjectId());

        //test second
        PatientFence fence1 = new PatientFence(patient, center1, radius1);

        fence1.save()
                .toBlocking()
                .single();

        assertNotNull(fence1.getObjectId());

        //test third
        PatientFence fence2 = new PatientFence(patient, center2, radius2);
        fence2.save()
                .toBlocking()
                .single();

        assertNotNull(fence2.getObjectId());


        //read test
        List<PatientFence> patientFences = PatientFence.findPatientFences(patient).toBlocking().single();

        assertNotNull(patientFences);
        assertEquals(patientFences.size(), 3);

        // test for adding fence into cached list then do a edit
        PatientFence fence4 = new PatientFence(patient, center1, radius1);

        patientFences.add(fence4.save()
                .toBlocking()
                .single());

        PatientFence fenceToEdit = patientFences.get(3);
        fenceToEdit.setCenter(center2);

        fenceToEdit.save()
                .toBlocking()
                .single();


        List<PatientFence> patientFences1 = PatientFence.findPatientFences(patient).toBlocking().single();

        PatientFence editedFence = patientFences1.get(0);

        assertEquals(editedFence.getCenter(), center2);


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

    /**
     * Tears down the test methods.
     *
     * @return Nothing.
     */
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
