package com.justzed.caretaker;

import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;


/**
 * This activity tests the MessengerActivity Class.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-09-29
 */
@Config(constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class MessengerActivityTest {
    //Variables
    MessengerActivity messenger;
    Button sendButton;
    //Constants
    private final String EMPTY_STRING = "";
    private final String NORMAL_STRING = "Hello my name is Hello World!";



    /**
     * Sets up the tests.
     *
     * @return Nothing.
     */
    @Before
    public void setUp(){

    }

    /**
     * Test passes no exceptions are thrown when running checkIfSendButtonNeeded
     * with no words supplied and the send button is disabled.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfSendButtonNeededNoWords(){
        messenger.checkIfSendButtonNeeded(EMPTY_STRING);


    }

    /**
     * Test passes no exceptions are thrown when running checkIfSendButtonNeeded
     * with words supplied and the send button is enabled.
     *
     * @return Nothing.
     */
    @Test
    public void testCheckIfSendButtonNeededWithWords(){
        messenger.checkIfSendButtonNeeded(NORMAL_STRING);

    }
}
