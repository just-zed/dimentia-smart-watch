package com.justzed.patient.internal.di;

import com.justzed.patient.Application;
import com.justzed.patient.internal.di.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by freeman on 8/17/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    Application injectApplication(Application application);
}
