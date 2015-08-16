package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.justzed.caretaker.Application;
import com.justzed.common.models.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by freeman on 8/16/15.
 * sequential tests covers CRD (no update) operations for Person
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

    @SmallTest
    public void testConstructer() {

        Person person = new Person(Person.CARETAKER, testToken);
        assertEquals(person.getUniqueToken(), testToken);
        assertNotSame(person.getType(), Person.PATIENT);
        assertEquals(person.getType(), Person.CARETAKER);
    }

    //create
    @Test
    public void testCreate() {
        //test uniqueness
        Person person1 = new Person(Person.PATIENT, testToken);
        Person retPerson1 = person1
                .save()
                .toBlocking()
                .first();

        assertNotNull(retPerson1.getObjectId());
        assertEquals(person.getObjectId(), retPerson1.getObjectId());
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
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
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
