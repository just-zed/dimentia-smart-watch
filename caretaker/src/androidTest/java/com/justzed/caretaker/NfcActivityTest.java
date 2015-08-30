package com.justzed.caretaker;

/**
 * Created by Nam Cuong on 28/08/2015.
 */

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.nfc.NfcAdapter;

import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NfcActivityTest extends ActivityTestCase{
    private NfcAdapter mNfcAdapter;
    private NfcActivity mNfcActivity;


    @Before
    protected void setUp() throws Exception {
        super.setUp();
        mNfcActivity = new NfcActivity();
        try {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(mNfcActivity);
        }
        catch(Exception e){}
    }

    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testReceiveNdefMsg(){
        mNfcActivity.receiveNdefMessage();
    }

    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testCheckNFCHardware(){
        mNfcActivity.checkNFCHardware();
    }

    /**
     * Fail the test if any exceptions occur.
     */
    @Test
    public void testCheckNFCEnabled(){
        mNfcActivity.checkNFCEnabled(mNfcAdapter);
    }

}
