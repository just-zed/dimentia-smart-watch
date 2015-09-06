package com.justzed.patient;

import com.parse.ParsePush;

/**
 * Created by takaha on 23/08/2015.
 */
public class NotificationMessage {

    public static void sendMessage(String channel, String message) {
        ParsePush push = new ParsePush();
        push.setChannel(channel);
        push.setMessage(message);
        push.sendInBackground();

    }

}
