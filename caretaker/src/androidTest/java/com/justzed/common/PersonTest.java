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
 * Created by freeman on 8/16/15.
 * tests covers CRD (no update) operations for Person
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PersonTest extends ApplicationTestCase<Application> {
    private static final String TAG = PersonTest.class.getName();

    private final String testToken = "someyadayadahardcodedtoken";

    private Person person;

    public PersonTest() {
        super(Application.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();


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
                .getByUniqueToken(testToken)
                .toBlocking()
                .first();

        assertNotNull(person1);
        assertEquals(person1.getUniqueToken(), person.getUniqueToken());
        assertEquals(person1.getUniqueToken(), testToken);
        assertEquals(person1.getType(), person.getType());
        assertTrue(person1.getType() == Person.PATIENT);
    }

    //delete
    @Test
    public void testDelete() {

        try {
            assertNull(person.delete().toBlocking().single());
            assertNull(person.getObjectId());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            assertNull(person.delete().toBlocking().single());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        Person person1 = Person
                .getByUniqueToken(testToken)
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
        try {
            assertNull(person.delete().toBlocking().single());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        person = null;

        super.tearDown();
    }
}
