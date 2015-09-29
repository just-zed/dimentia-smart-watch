package com.justzed.caretaker.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.justzed.caretaker.DialogActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a custom Push Notification receiver that catches ParsePush notifications
 * and show them as alert dialogs
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-8-22
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = PushBroadcastReceiver.class.getSimpleName();


    private static final String KEY_ALERT = "alert";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        // do normal notification
        super.onPushReceive(context, intent);

        // then run custom alert
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, json.toString());

            if (json.has(KEY_ALERT)) {
                Intent dialogIntent = new Intent(context, DialogActivity.class);
                dialogIntent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, json.getString(KEY_ALERT));

                // run this as new task
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(dialogIntent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
