package com.justzed.caretaker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * This is a "Dialog" activity that can be called using startActivity() and this displays an transparent
 * activity and show a alertDialog in middle with
 * <p>
 * a positive button to launch CareTaker app and
 * a negative button to simply close the dialog and this activity
 *
 * @author Freeman Man
 * @since 2015-9-20
 */
public class DialogActivity extends Activity {

    public static String INTENT_KEY_MESSAGE = "DialogMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (getIntent() == null) {
            // make sure it is called using intent
            finish();
        } else {
            CharSequence message = getIntent()
                    .getStringExtra(INTENT_KEY_MESSAGE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.launch_caretaker,
                            (dialog, which) -> {
                                // launch main activity
                                // TODO: fix app workflow so we can launch MapActivity instead.
                                // currently we cannot do that as we requires MainActivity to get the patient
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            })
                    .setNegativeButton(android.R.string.ok,
                            ((dialog1, which1) -> {
                                dialog1.cancel();
                                finish();
                            }));

            builder.show();

        }

    }
}
