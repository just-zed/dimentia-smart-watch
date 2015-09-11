package com.justzed.patient;

import com.parse.ParsePush;

/**
 * Created by Hiroki Takahashi on 23/08/2015.
 * This class is used to send a message (push notification) via Parse.com
 *
 */
public class NotificationMessage {

    /*This is used to send a message in a particular channel.
    * @param channel  the channel name
    * @param  message the message itself
    * @pre
    * @post
    */
      public static void sendMessage(String channel, String message) {
        ParsePush push = new ParsePush();
        push.setChannel(channel);
        push.setMessage(message);
        push.sendInBackground();

    }

}
