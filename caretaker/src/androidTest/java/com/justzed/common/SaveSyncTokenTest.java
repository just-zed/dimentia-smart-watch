package com.justzed.caretaker;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.justzed.common.SaveSyncToken;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Shirin on 8/30/2015.
 */
@RunWith(AndroidJUnit4.class)
public class SaveSyncTokenTest extends ActivityTestCase {

    public SaveSyncTokenTest() {
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        //setup
    }

    @Test
    public void testSaveSyncToken(){
        //put tests here

        //saveSyncToken.findMyDeviceId();

        Activity activity = new MainActivity();
        SaveSyncToken saveSyncToken = new SaveSyncToken(activity);

        // Activity activity1 = new MainActivity();
        //SaveSyncToken saveSyncToken1 = new SaveSyncToken(activity1);

        //assertNotSame(activity.hashCode(), activity1.hashCode());

        String testToken = saveSyncToken.findMyDeviceId();
        // String testToken1 = saveSyncToken1.findMyDeviceId();

        assertNotNull(testToken);
        //assertNotSame(testToken, "");
//
        //assertEquals(testToken, testToken1);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        //de-setup
    }
}

