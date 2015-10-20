package com.justzed.caretaker;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Custom Application class that initiates and setup Parse.com libraries
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-8-22
 */
public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }

}
