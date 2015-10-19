package com.justzed.patient;

import android.nfc.NfcAdapter;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.runner.RunWith;

/**
 * This class is used to test the TokenSenderHelper class for the TokenSenderActivity.
 *
 * @author Tristan Dubois
 * @version 1.0
 * @since 2015-10-16
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class TokenSenderHelperTest extends TestCase {
    //Variables
    public TokenSenderHelper sendToken = new TokenSenderHelper();
    //Constants
    private String TEST_PATIENT_INFORMATION = "101,Jenny,Patient,f07a13984f6d116a";
    private String EMPTY_TEST_PATIENT_INFORMATION = "";
    private String SMALL_TEST_PATIENT_INFORMATION = "1,J,Patient,f";
    private String LARGE_TEST_PATIENT_INFORMATION = "1013215413456453135,"
            +"Jenny Of the woods who also likes to be called bob during her birthdays,"
            +"Patient,"
            +"f07a13984f6d116a010101000010101010101010010101010101010101010101010101010";


    private String EXPECTED_PATIENT_INFORMATION_FROM_NDEF_MESSAGE = "101,Jenny,Patient,f07a13984f6d116a";
    private String EXPECTED_EMPTY_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE = "";
    private String EXPECTED_SMALL_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE = "1,J,Patient,f";
    private String EXPECTED_LARGE_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE = "1013215413456453135,"
            +"Jenny Of the woods who also likes to be called bob during her birthdays,"
            +"Patient,"
            +"f07a13984f6d116a010101000010101010101010010101010101010101010101010101010";

    @Before
    public void createTokenSenderActivity(){
        sendToken = new TokenSenderHelper();
    }

    /**
     * Fail the test if any exceptions occur.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderException(){

        sendToken.messageBuilder(TEST_PATIENT_INFORMATION);
    }

    /**
     * Fail the test if a normal string cannot be retrieved from the magic or does not equal the
     * expected value.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderCheckContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals( EXPECTED_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }

    /**
     * Fail the test if the message is null when it is retrieved.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderCheckNotNull(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertNotNull(EXPECTED_PATIENT_INFORMATION_FROM_NDEF_MESSAGE);
    }

    /**
     * Fail the test if a Small string cannot be retrieved from the magic or does not equal the
     * expected value.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderCheckSmallContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(SMALL_TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals(EXPECTED_SMALL_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }

    /**
     * Fail the test if a Large string cannot be retrieved from the magic or does not equal the
     * expected value.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderCheckLargeContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(LARGE_TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals(EXPECTED_LARGE_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }

    /**
     * Fail the test if an Empty string cannot be retrieved from the magic or does not equal the
     * expected value.
     *
     * @return Nothing.
     */
    @Test
    public void testMessageBuilderCheckEmptyContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(EMPTY_TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals(EXPECTED_EMPTY_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }
}
