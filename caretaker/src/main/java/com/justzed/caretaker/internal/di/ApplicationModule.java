package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.Application;
import com.parse.Parse;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.justzed.common.R.string.PARSE_APPLICATION_KEY;
import static com.justzed.common.R.string.PARSE_CLIENT_KEY;


/**
 * Created by freeman on 8/17/15.
 */
@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;


        Parse.initialize(application,
                application.getString(PARSE_APPLICATION_KEY),
                application.getString(PARSE_CLIENT_KEY));

    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
