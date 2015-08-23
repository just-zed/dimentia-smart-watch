package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.CaretakerApplication;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class CaretakerApplicationModule_ProvideApplicationFactory implements Factory<CaretakerApplication> {
  private final CaretakerApplicationModule module;

  public CaretakerApplicationModule_ProvideApplicationFactory(CaretakerApplicationModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public CaretakerApplication get() {  
    CaretakerApplication provided = module.provideApplication();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<CaretakerApplication> create(CaretakerApplicationModule module) {  
    return new CaretakerApplicationModule_ProvideApplicationFactory(module);
  }
}

