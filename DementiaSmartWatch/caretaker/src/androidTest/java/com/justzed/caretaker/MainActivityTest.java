package com.justzed.caretaker;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by freeman on 8/16/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest extends ActivityTestCase {
    public MainActivityTest() {
        super();
        setActivity(new MainActivity());
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void someTest() {

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
