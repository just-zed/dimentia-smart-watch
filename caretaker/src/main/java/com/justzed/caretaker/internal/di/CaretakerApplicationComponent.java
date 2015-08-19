package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.CaretakerApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by freeman on 8/17/15.
 */
@Singleton
@Component(modules = {CaretakerApplicationModule.class})
public interface CaretakerApplicationComponent {
    CaretakerApplication injectApplication(CaretakerApplication application);
}
