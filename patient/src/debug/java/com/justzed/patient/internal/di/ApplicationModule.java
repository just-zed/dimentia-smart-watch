package com.justzed.patient.internal.di;

import com.justzed.common.ApiKeys;
import com.justzed.patient.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by freeman on 8/17/15.
 */
@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;


        Parse.initialize(application,
                ApiKeys.PARSE_API_TEST_APPLICATION_ID,
                ApiKeys.PARSE_API_TEST_CLIENT_KEY);



    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
