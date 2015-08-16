package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

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


    public PersonTest() {
        super(Application.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testConstructer() {

        Person person = new Person(Person.CARETAKER, testToken);
        assertEquals(person.getUniqueToken(), testToken);
        assertNotSame(person.getType(), Person.PATIENT);
        assertEquals(person.getType(), Person.CARETAKER);
    }

    //create
    @Test
    public void testCreate() {

        //test creation
        Person person = new Person(Person.PATIENT, testToken);

        Person retPerson = person
                .save()
                .toBlocking()
                .first();

        assertNotNull(retPerson.getObjectId());


        //test uniqueness
        Person person1 = new Person(Person.PATIENT, testToken);
        Person retPerson1 = person1
                .save()
                .toBlocking()
                .first();

        assertNotNull(retPerson1.getObjectId());
        assertEquals(retPerson.getObjectId(), retPerson1.getObjectId());
    }

    //read
    @Test
    public void testRead() {
        Person person = Person
                .getByUniqueToken(testToken)
                .toBlocking()
                .first();

        assertNotNull(person);
        assertEquals(person.getUniqueToken(), testToken);
        assertTrue(person.getType() == Person.PATIENT);
    }

    //delete
    @Test
    public void testDelete() {
        Person person = Person
                .getByUniqueToken(testToken)
                .toBlocking()
                .first();
        assertNotNull(person);
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


    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
