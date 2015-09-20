package com.justzed.caretaker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

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
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
