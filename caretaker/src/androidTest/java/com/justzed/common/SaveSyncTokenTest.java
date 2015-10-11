package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the SaveSyncToken Class.
 *
 * @author Shirin Azizmohammad
 * @version 1.0
 * @since 2015-08-30
 */
@RunWith(AndroidJUnit4.class)
public class SaveSyncTokenTest extends AndroidTestCase {


    public SaveSyncTokenTest() {
    }

    /**
     * Sets up the tests.
     *
     * @return Nothing.
     */
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        //setup
    }

    /**
     * Fails if a Exception occurs.
     *
     * @return Nothing.
     */
    @Test
    public void testSaveSyncToken() {
        //put tests here

        SaveSyncToken saveSyncToken = new SaveSyncToken(getContext());

        String testToken = saveSyncToken.findMyDeviceId();

        assertNotNull(testToken);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        //de-setup
    }
}

