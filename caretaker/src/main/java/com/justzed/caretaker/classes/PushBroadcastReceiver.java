package com.justzed.caretaker.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.justzed.caretaker.DialogActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by freeman on 9/20/15.
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = PushBroadcastReceiver.class.getSimpleName();


    private static final String KEY_ALERT = "alert";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        // pop notification
        super.onPushReceive(context, intent);

        // then run custom alert
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, json.toString());

            if (json.has(KEY_ALERT)) {
                Intent dialogIntent = new Intent(context, DialogActivity.class);
                dialogIntent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, json.getString(KEY_ALERT));
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(dialogIntent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
