package com.justzed.caretaker;

import com.justzed.caretaker.internal.di.ApplicationComponent;
import com.justzed.caretaker.internal.di.ApplicationModule;
import com.justzed.caretaker.internal.di.DaggerApplicationComponent;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Custom Application class that initiates and setup Parse.com libraries
 *
 * @author Freeman Man
 * @since 2015-8-22
 */
public class Application extends android.app.Application {

    ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        component = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this)).build();

        component.injectApplication(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
