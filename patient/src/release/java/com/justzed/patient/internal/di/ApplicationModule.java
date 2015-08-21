package com.justzed.patient.internal.di;

import com.justzed.common.ApiKeys;
import com.justzed.patient.Application;
import com.parse.Parse;

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
        Parse.enableLocalDatastore(application);
        Parse.initialize(application,
                ApiKeys.PARSE_API_PROD_APPLICATION_ID,
                ApiKeys.PARSE_API_PROD_CLIENT_KEY);
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
