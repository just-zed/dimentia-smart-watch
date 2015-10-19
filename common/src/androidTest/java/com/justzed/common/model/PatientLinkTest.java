package com.justzed.common.model;

import android.app.Application;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.justzed.common.TestSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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

    private String patientToken;
    private String caretakerToken;

    private Person patient;
    private Person caretaker;
    private Person patient1;
    private Person caretaker1;
    private Person patient2;
    private Person caretaker2;


    public PatientLinkTest() {
        super(Application.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        patientToken = "test_patient_" + Math.random() * 1000;
        caretakerToken = "test_caretaker_" + Math.random() * 1000;

        String patientToken1 = "test_patient_" + Math.random() * 1000;
        String caretakerToken1 = "test_caretaker_" + Math.random() * 1000;

        String patientToken2 = "test_patient_" + Math.random() * 1000;
        String caretakerToken2 = "test_caretaker_" + Math.random() * 1000;

        try {

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

            //test creation
            patient1 = new Person(Person.PATIENT, patientToken1)
                    .save()
                    .toBlocking()
                    .single();

            assertNotNull(patient1.getObjectId());

            //test creation
            caretaker1 = new Person(Person.CARETAKER, caretakerToken1)
                    .save()
                    .toBlocking()
                    .single();

            assertNotNull(caretaker1.getObjectId());

            //test creation
            patient2 = new Person(Person.PATIENT, patientToken2)
                    .save()
                    .toBlocking()
                    .single();

            assertNotNull(patient2.getObjectId());

            //test creation
            caretaker2 = new Person(Person.CARETAKER, caretakerToken2)
                    .save()
                    .toBlocking()
                    .single();

            assertNotNull(caretaker2.getObjectId());
        } catch (Exception e) {
            TestSetup.setupParse(getContext());
        }

    }


    @Test
    public void test1Init() {
        // hack to init application
        assertTrue(true);
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

    @LargeTest
    public void testSearch() {

        assertNull(PatientLink.findLatestByPatient(patient).toBlocking().single());

        //setUp

        /**
         * test links
         *
         * patient - caretaker
         * patient1 - caretaker
         * patient2 - caretaker1
         * patient - caretaker1
         * patient - caretaker2
         *
         */


        PatientLink link0 = new PatientLink(patient, caretaker)
                .save()
                .toBlocking()
                .single();

        PatientLink link1 = new PatientLink(patient1, caretaker)
                .save()
                .toBlocking()
                .single();

        PatientLink link2 = new PatientLink(patient2, caretaker1)
                .save()
                .toBlocking()
                .single();

        PatientLink link3 = new PatientLink(patient, caretaker1)
                .save()
                .toBlocking()
                .single();

        PatientLink link4 = new PatientLink(patient, caretaker2)
                .save()
                .toBlocking()
                .single();

        //latest link to patient is caretaker2
        assertEquals(PatientLink.findLatestByPatient(patient).toBlocking().single().getCaretaker().getObjectId(), caretaker2.getObjectId());

        //latest link to caretaker is patient1
        assertEquals(PatientLink.findLatestByCaretaker(caretaker).toBlocking().single().getPatient().getObjectId(), patient1.getObjectId());


        List<PatientLink> caretakerPatientLinks = PatientLink.findAllByCaretaker(caretaker).toBlocking().single();

        //number of links to caretaker is 2
        assertEquals(caretakerPatientLinks.size(), 2);
        assertEquals(caretakerPatientLinks.get(0).getPatient().getObjectId(), patient1.getObjectId());

        List<PatientLink> patientPatientLinks = PatientLink.findAllByPatient(patient).toBlocking().single();

        //number of links to patient is 3
        assertEquals(patientPatientLinks.size(), 3);
        assertEquals(patientPatientLinks.get(0).getCaretaker().getObjectId(), caretaker2.getObjectId());


        //tearDown

        // loop does not work here because these are observables
        assertNull(link0.delete().toBlocking().single());
        assertNull(link1.delete().toBlocking().single());
        assertNull(link2.delete().toBlocking().single());
        assertNull(link3.delete().toBlocking().single());
        assertNull(link4.delete().toBlocking().single());

    }

    @After
    protected void tearDown() throws Exception {
        try {
            assertNull(patient.delete().toBlocking().single());
            assertNull(caretaker.delete().toBlocking().single());
            assertNull(patient1.delete().toBlocking().single());
            assertNull(caretaker1.delete().toBlocking().single());
            assertNull(patient2.delete().toBlocking().single());
            assertNull(caretaker2.delete().toBlocking().single());
        } catch (Exception e) {
            // hack to get Parse.com stuff working under library test cases
        }


        patient = null;
        caretaker = null;
        patient1 = null;
        caretaker1 = null;
        patient2 = null;
        caretaker2 = null;

        super.tearDown();
    }
}
