package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    Application injectApplication(Application application);
}
