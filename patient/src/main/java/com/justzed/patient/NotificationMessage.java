package com.justzed.patient;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.LinkedList;

/**
 * Created by takaha on 23/08/2015.
 */
public class NotificationMessage {
    private final ParsePush push = new ParsePush();

    public void sendMessage(String message) {
        push.setMessage(message);
        push.sendInBackground();
        
    }

}
