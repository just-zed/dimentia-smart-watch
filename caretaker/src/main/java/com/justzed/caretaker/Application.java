package com.justzed.caretaker;

import com.justzed.caretaker.internal.di.ApplicationComponent;
import com.justzed.caretaker.internal.di.ApplicationModule;
import com.justzed.caretaker.internal.di.DaggerApplicationComponent;

/**
 * Created by freeman on 8/16/15.
 */
public class Application extends android.app.Application {

    ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();


        component = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this)).build();

        component.injectApplication(this);
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
