package com.justzed.patient;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.justzed.common.NotificationMessage;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Hiroki Takahashi
 * @since 2015-9-10
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationMessageTest extends TestCase {
    //An object of NotificationMessage.java
    NotificationMessage NM = new NotificationMessage();

    //In this test, a simple message is sent
    @Test
    public void testSendMessageException() {
        NotificationMessage.sendMessage("testChannel", "testMessage");
    }
}
