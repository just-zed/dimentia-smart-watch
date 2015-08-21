package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.Application;
import com.justzed.common.ApiKeys;
import com.parse.Parse;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by freeman on 8/17/15.
 */
@Module
public class CaretakerApplicationModule {

    private final Application application;

    public CaretakerApplicationModule(Application application) {
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
