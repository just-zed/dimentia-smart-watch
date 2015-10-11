package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.justzed.common.NotificationMessage;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Hiroki Takahashi on 10/09/2015.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationMessageTest extends TestCase {
    //An object of NotificationMessage.java
    NotificationMessage NM = new NotificationMessage();

    //In this test, a simple message is sent
    @Test
    public void testSendMessageException(){
       NM.sendMessage("testChannel", "testMessage");
    }
}
