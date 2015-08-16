package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.justzed.caretaker.Application;
import com.justzed.common.models.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by freeman on 8/16/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class PersonTest extends ApplicationTestCase<Application> {
    private static final String TAG = PersonTest.class.getName();

    private String testPersonId;
    private final String testToken = "somehardcodedtoken";
    private final String testToken2 = "someotherhardcodedtoken";


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

        Person person = new Person(Person.PATIENT, testToken);

        Person retPerson = person
                .save()
                .toBlocking()
                .first();

        assertNotNull(retPerson.getObjectId());
        testPersonId = retPerson.getObjectId();
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
            person.delete().toBlocking().single();
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
