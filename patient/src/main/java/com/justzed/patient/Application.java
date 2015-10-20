package com.justzed.patient;

import com.parse.Parse;
import com.parse.ParseInstallation;

import static com.justzed.common.R.string.PARSE_APPLICATION_KEY;
import static com.justzed.common.R.string.PARSE_CLIENT_KEY;

/**
 * Created by freeman on 8/16/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                this.getString(PARSE_APPLICATION_KEY),
                this.getString(PARSE_CLIENT_KEY));

        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
