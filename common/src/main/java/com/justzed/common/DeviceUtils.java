package com.justzed.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Device Utilities
 *
 * @author Freeman Man
 * @since 2015-10-11
 */
public class DeviceUtils {

    /**
     * Get owner name of the device
     *
     * @param context       context
     * @param defaultString string to return if context is null
     * @return defaultString if context is null, or device's owner name
     */
    public static String getDeviceOwnerName(Context context, String defaultString) {
        String ownerName = defaultString;

        if (context != null) {
            Cursor c = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                ownerName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                c.close();
            }
        }

        return ownerName;
    }
}
