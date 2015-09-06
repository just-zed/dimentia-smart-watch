package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by takaha on 30/08/2015.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PanicButtonActivityTest extends ActivityTestCase {
   private final PanicButtonActivity PB = new PanicButtonActivity();


    @Test
    public void testSendMessageException(){
        PB.sendMessage("PanicButtonActivity test");
    }

}
