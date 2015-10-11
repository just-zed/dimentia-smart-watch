package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.justzed.caretaker.Application;
import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * tests covers CRD operations for PatientLink
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-08-18
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientLinkTest extends ApplicationTestCase<Application> {
    private static final String TAG = PatientLinkTest.class.getName();

    private final String patientToken = "test_patient_" + Math.random() * 1000;
    private final String caretakerToken = "test_caretaker_" + Math.random() * 1000;


    private Person patient;
    private Person caretaker;

    public PatientLinkTest() {
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

        //test creation
        caretaker = new Person(Person.CARETAKER, caretakerToken)
                .save()
                .toBlocking()
                .single();

        assertNotNull(caretaker.getObjectId());
    }

    @Test
    public void testConstructer() {
        PatientLink link = new PatientLink(patient, caretaker);
        assertEquals(link.getPatient().getType(), Person.PATIENT);
        assertEquals(link.getPatient().getUniqueToken(), patientToken);
        assertEquals(link.getCaretaker().getType(), Person.CARETAKER);
        assertEquals(link.getCaretaker().getUniqueToken(), caretakerToken);

    }

    //create
    @Test
    public void testCreateDelete() {

        //test create
        PatientLink link = new PatientLink(patient, caretaker)
                .save()
                .toBlocking()
                .single();

        assertNotNull(link.getObjectId());


        //test duplicate

        PatientLink link1 = new PatientLink(patient, caretaker)
                .save()
                .toBlocking()
                .single();

        assertNotNull(link1.getObjectId());
        assertEquals(link.getObjectId(), link1.getObjectId());

        //test delete

        try {
            assertNull(link.delete().toBlocking().single());
            assertNull(link.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        // double delete
        try {
            assertNull(link.delete().toBlocking().single());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

    }

    //read
    @Test
    public void testRead() {

        //setUp
        PatientLink createLink = new PatientLink(patient, caretaker)
                .save()
                .toBlocking()
                .single();

        assertNotNull(createLink.getObjectId());


        //read test
        PatientLink link = PatientLink.findByPersons(patient, caretaker)
                .toBlocking().single();

        assertNotNull(link);
        assertEquals(link.getPatient().getObjectId(), patient.getObjectId());
        assertEquals(link.getCaretaker().getObjectId(), caretaker.getObjectId());

        //tearDown


        try {
            assertNull(link.delete().toBlocking().single());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @After
    protected void tearDown() throws Exception {
        try {
            assertNull(patient.delete().toBlocking().single());
            assertNull(caretaker.delete().toBlocking().single());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        patient = null;
        caretaker = null;

        super.tearDown();
    }
}
