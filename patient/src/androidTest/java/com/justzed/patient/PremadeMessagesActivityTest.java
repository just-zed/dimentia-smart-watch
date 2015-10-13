package com.justzed.patient;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.justzed.common.model.Person;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;

/**
 * Testing is broken (11/10/2015)
 *
 * @author Hiroki Takahashi
 * @since 2015-10-6
 */
@RunWith(AndroidJUnit4.class)
public class PremadeMessagesActivityTest extends AndroidTestCase {

    /**
     * http://blog.sqisland.com/2015/04/espresso-21-activitytestrule.html
     * https://google.github.io/android-testing-support-library/docs/espresso/index.html
     */
    @Rule
    public ActivityTestRule<PremadeMessagesActivity> mActivityRule =
            new ActivityTestRule<>(
                    PremadeMessagesActivity.class,
                    true,
                    false);


    /*
     * Set up testing
     * @return nothing
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        Person patient = new Person(Person.PATIENT, "testToken");
        patient.setName("patient");

        Intent intent = new Intent();
        intent.putExtra(Person.PARCELABLE_KEY, patient);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void testOK() {
        // one of the message in hardcoded list
        // there should be only 1 match
        onData(hasToString(equalTo("OK")))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0)
                .perform(click());
    }

    @Test
    public void testYes() {
        // one of the message in hardcoded list
        // there should be only 1 match
        onData(hasToString(equalTo("Yes")))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0)
                .perform(click());
    }

    /*
     * Tear down testing
     * @return noting
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
