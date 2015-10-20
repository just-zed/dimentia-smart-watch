package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

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
public class ContextUtilsTest extends AndroidTestCase {

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
}

