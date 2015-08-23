package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.nfc.NfcAdapter;

import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Tristan on 14/08/2015.
 *
 * This class is used to test the TokenSenderActivity Class.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TokenSenderActivityTest extends ActivityTestCase{
    //Variables
    private NfcAdapter mNfcAdapter;
    public TokenSenderActivity sendToken = new TokenSenderActivity();
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
        sendToken = new TokenSenderActivity();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(sendToken);
    }


    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testSendPatientDataWithNFCException() {
        sendToken.sendPatientDataWithNFC();
    }


    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testCheckNfcException() {
        sendToken.checkNfc(mNfcAdapter);
    }

    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testMessageBuilderException(){

        sendToken.messageBuilder(TEST_PATIENT_INFORMATION);
    }

    /**
     * Fail the test if a normal string cannot be retrieved from the magic or does not equal the
     * expected value.
     */
    @Test
    public void testMessageBuilderCheckContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals( EXPECTED_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }

    /**
     * Fail the test if the message is null when it is retrieved.
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
     */
    @Test
    public void testMessageBuilderCheckEmptyContent(){
        String stringMessage;
        stringMessage = new String(sendToken.messageBuilder(EMPTY_TEST_PATIENT_INFORMATION).getRecords()[0].getPayload());

        assertTrue(stringMessage.equals(EXPECTED_EMPTY_TEST_PATIENT_INFORMATION_FROM_NDEF_MESSAGE));
    }
}

