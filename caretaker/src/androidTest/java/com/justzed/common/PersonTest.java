package com.justzed.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.justzed.caretaker.Application;
import com.justzed.common.model.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * tests covers CRUD operations for Person
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-08-16
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PersonTest extends ApplicationTestCase<Application> {
    private static final String TAG = PersonTest.class.getName();

    private String testToken;
    private final boolean testDGchecks = false;

    private Person person;

    public PersonTest() {
        super(Application.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        testToken = "test_person_" + Math.random() * 1000;
        //test creation
        person = new Person(Person.PATIENT, testToken)
                .save()
                .toBlocking()
                .first();

        assertNotNull(person.getObjectId());
    }

    @Test
    public void testConstructer() {

        Person person = new Person(Person.CARETAKER, testToken);
        assertEquals(person.getUniqueToken(), testToken);
        assertNotSame(person.getType(), Person.PATIENT);
        assertEquals(person.getType(), Person.CARETAKER);
        assertEquals(person.getDisableGeofenceChecks(), testDGchecks);
    }

    @Test
    public void testParcelable() {
        Intent intent = new Intent();
        intent.putExtra(Person.PARCELABLE_KEY, person);

        //

        Bundle data = intent.getExtras();
        assertNotNull(data);

        Person person1 = data.getParcelable(Person.PARCELABLE_KEY);
        assertNotNull(person1);
        assertEquals(person.getObjectId(), person1.getObjectId());

    }

    //create
    @Test
    public void testCreate() {
        //test uniqueness
        Person person1 = new Person(Person.PATIENT, testToken)
                .save()
                .toBlocking()
                .first();
        assertNotNull(person1.getObjectId());
        assertEquals(person.getObjectId(), person1.getObjectId());
        assertEquals(person1.getType(), Person.PATIENT);
        assertEquals(person1.getDisableGeofenceChecks(), testDGchecks);

        //test if person is updated to caretaker
        Person person2 = new Person(Person.CARETAKER, testToken)
                .save()
                .toBlocking()
                .first();
        assertNotNull(person2.getObjectId());
        assertEquals(person2.getType(), Person.CARETAKER);

        //set it back to patient
        Person person3 = new Person(Person.PATIENT, testToken)
                .save()
                .toBlocking()
                .first();
        assertEquals(person3.getType(), Person.PATIENT);
        assertNotNull(person3.getObjectId());
        assertEquals(person3.getType(), Person.PATIENT);

    }

    //read
    @Test
    public void testRead() {
        //read

        Person person1 = Person
                .findByUniqueToken(testToken)
                .toBlocking()
                .first();

        assertNotNull(person1);
        assertEquals(person1.getUniqueToken(), person.getUniqueToken());
        assertEquals(person1.getUniqueToken(), testToken);
        assertEquals(person1.getType(), person.getType());
        assertTrue(person1.getType() == Person.PATIENT);
        assertEquals(person1.getDisableGeofenceChecks(), testDGchecks);

    }


    public void testEdit() {

        Person person1 = Person
                .findByUniqueToken(testToken)
                .toBlocking()
                .first();

        assertNotNull(person1);
        assertEquals(person1.getUniqueToken(), person.getUniqueToken());
        assertEquals(person1.getUniqueToken(), testToken);
        assertEquals(person1.getType(), person.getType());
        assertTrue(person1.getType() == Person.PATIENT);
        assertEquals(person1.getDisableGeofenceChecks(), false);

        // set disableGeoFence flag
        person1.setDisableGeofenceChecks(true);

        // save the modified person
        Person peron1AfterSave = person1.save().toBlocking().single();

        // the saved person is the same as the previous person
        assertEquals(person1.getObjectId(), peron1AfterSave.getObjectId());

        // check the saved changes
        assertEquals(peron1AfterSave.getDisableGeofenceChecks(), true);
    }

    //delete
    @Test
    public void testDelete() {

        assertNull(person.delete().toBlocking().single());
        assertNull(person.getObjectId());

        try {
            assertNull(person.delete().toBlocking().single());
            assertTrue(false);
        } catch (Exception e) {
            // re-delete throws exception
            assertTrue(true);
        }

        Person person1 = Person
                .findByUniqueToken(testToken)
                .toBlocking()
                .first();

        assertNull(person1);


        //test creation
        person = new Person(Person.PATIENT, testToken)
                .save()
                .toBlocking()
                .first();

        assertNotNull(person.getObjectId());

    }

    @After
    protected void tearDown() throws Exception {
        assertNull(person.delete().toBlocking().single());

        person = null;

        super.tearDown();
    }
}
