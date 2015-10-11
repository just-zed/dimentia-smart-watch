package com.justzed.caretaker;

import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * This activity tests the MessengerActivity Class.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-09-29
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessengerActivityTest extends ActivityInstrumentationTestCase2<MessengerActivity> {
    //Variables
    Button sendButton;
    private MessengerActivity messenger;
    //Constants
    private final String EMPTY_STRING = "";
    private final String NORMAL_STRING = "Hello my name is Hello World!";

    public MessengerActivityTest() {
        super(MessengerActivity.class);
    }

    /**
     * Sets up the tests.
     *
     * @return Nothing.
     */
    /*@Before
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        messenger = getActivity();

    }*/

    /**
     * Test passes no exceptions are thrown when running checkIfSendButtonNeeded
     * with no words supplied and the send button is disabled.
     *
     * @return Nothing.
     */
    /*@Test
    public void testCheckIfSendButtonNeededNoWords(){
        //messenger.checkIfSendButtonNeeded(EMPTY_STRING);

        onView(withId(R.id.send_button)).perform(click());



    }*/

    /**
     * Test passes no exceptions are thrown when running checkIfSendButtonNeeded
     * with words supplied and the send button is enabled.
     *
     * @return Nothing.
     */
    /*@Test
    public void testCheckIfSendButtonNeededWithWords(){
        messenger.checkIfSendButtonNeeded(NORMAL_STRING);
        onView(withId(R.id.messenger)).perform(typeText(NORMAL_STRING), closeSoftKeyboard());

        onView(withId(R.id.send_button)).perform(click());
    }*/
}
