package com.justzed.common;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * This class adds a new row to the PersonLink table of the database and
 * syncs the DB to both devices.
 *
 * @author Shirin Azizmohammad
 * @version 1.0
 * @since 2015-08-15
 */
public class SaveSyncToken {

    private final Context context;

    public SaveSyncToken(Context context) {
        this.context = context;
    }

    /**
     * generate unique id by ANDROID_ID
     * <p>
     *
     * @return a String containing the device Id.
     */
    public String findMyDeviceId() {
        /**
         * TODO: check for android permissions, so it won't throw permission exception in API 23+
         */

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, androidId;
        tmDevice = "" + tm.getDeviceId();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32));

        return deviceUuid.toString();
    }
}