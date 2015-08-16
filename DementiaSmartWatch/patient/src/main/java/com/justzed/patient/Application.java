package com.justzed.patient;

import com.justzed.common.ApiKeys;
import com.parse.Parse;

/**
 * Created by freeman on 8/16/15.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, ApiKeys.PARSE_API_PROD_APPLICATION_ID, ApiKeys.PARSE_API_PROD_CLIENT_KEY);
    }
}
