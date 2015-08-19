package com.justzed.caretaker;

import com.justzed.caretaker.internal.di.CaretakerApplicationComponent;
import com.justzed.caretaker.internal.di.CaretakerApplicationModule;
import com.justzed.caretaker.internal.di.DaggerCaretakerApplicationComponent;

/**
 * Created by freeman on 8/16/15.
 */
public class CaretakerApplication extends android.app.Application {

    CaretakerApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();


        component = DaggerCaretakerApplicationComponent
                .builder()
                .caretakerApplicationModule(new CaretakerApplicationModule(this)).build();

        component.injectApplication(this);
    }

    public CaretakerApplicationComponent getComponent() {
        return component;
    }
}
