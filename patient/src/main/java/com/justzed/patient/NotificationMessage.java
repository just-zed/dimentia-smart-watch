package com.justzed.patient;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.LinkedList;

/**
 * Created by takaha on 23/08/2015.
 */
public class NotificationMessage {

    static LinkedList<String> channels = new LinkedList<String>();
    static ParsePush push = new ParsePush();

    public  void saveInstallation() {
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
    /*
    public  void subscribeUserInBacground(String subscriber) {
        // When users indicate they are Giants fans, we subscribe them to that channel.
        ParsePush.subscribeInBackground(subscriber);
    }*/

    /*
    public void addUser(String subscriber){
       //The simplest way to start sending notifications is using channels.
        //This allows you to use a publisher-subscriber model for sending pushes.
        channels.add(subscriber);
    } */

    public void setChannel(String channel){
        push.setChannel(channel);
    }

    public void setMessage(String message) {
        //SENDING PUSHES TO CHANNELS
        //This can be used to sent the message to the subscribers of the "channel"
        push.setMessage(message);
    }

    public  void sendMessage(){
        push.sendInBackground();
    }


      /* Associate the device with a user
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user",ParseUser.getCurrentUser());
        installation.saveInBackground();*/
}
