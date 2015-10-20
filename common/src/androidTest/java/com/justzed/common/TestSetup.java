package com.justzed.common;

import android.content.Context;

import com.parse.Parse;

/**
 * Created by freeman on 10/20/15.
 */
public class TestSetup {
    public static void setupParse(Context context) {
        Parse.enableLocalDatastore(context);
        Parse.initialize(context,
                "B6Mfu8m8nyLjFPPi2QuoJz1TYRyno73F3XtxTpT6",
                "5W3YdhccEiv1T1mnPJ5BJDiq6DlykO0h8hpWZtcM");
    }
}
