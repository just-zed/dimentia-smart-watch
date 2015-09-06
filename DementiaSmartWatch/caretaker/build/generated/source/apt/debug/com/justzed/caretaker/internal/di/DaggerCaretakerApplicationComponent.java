package com.justzed.caretaker.internal.di;

import com.justzed.caretaker.CaretakerApplication;
import dagger.internal.MembersInjectors;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class DaggerCaretakerApplicationComponent implements CaretakerApplicationComponent {
  private DaggerCaretakerApplicationComponent(Builder builder) {  
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {  
    return new Builder();
  }

  private void initialize(final Builder builder) {  
  }

  @Override
  public CaretakerApplication injectApplication(CaretakerApplication application) {  
    MembersInjectors.noOp().injectMembers(application);
    return application;
  }

  public static final class Builder {
    private CaretakerApplicationModule caretakerApplicationModule;
  
    private Builder() {  
    }
  
    public CaretakerApplicationComponent build() {  
      if (caretakerApplicationModule == null) {
        throw new IllegalStateException("caretakerApplicationModule must be set");
      }
      return new DaggerCaretakerApplicationComponent(this);
    }
  
    public Builder caretakerApplicationModule(CaretakerApplicationModule caretakerApplicationModule) {  
      if (caretakerApplicationModule == null) {
        throw new NullPointerException("caretakerApplicationModule");
      }
      this.caretakerApplicationModule = caretakerApplicationModule;
      return this;
    }
  }
}

