package com.justzed.common;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by freeman on 9/5/15.
 * <p>
 * This class contains helper function for location related operations
 *
 * @author Freeman
 * @version 1.0
 * @since 2015-8-24
 */
public class FenceUtils {

    private static final String TIME_FORMATTER = "HH:MM";
    private static final DateFormat df = new SimpleDateFormat(TIME_FORMATTER, Locale.ENGLISH);

    /**
     * convert ParseGeoPoint to google LatLng
     *
     * @param geoPoint ParseGeoPoint parse.com's geopoint type
     * @return LatLng Latitude and Longitude of a geofence.
     * @throws ParseException
     */
    public static LatLng toLatLng(ParseGeoPoint geoPoint) throws ParseException {
        if (geoPoint != null) {
            return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        } else {
            return null;
        }
    }

    /**
     * convert google LatLng to ParseGeoPoint
     *
     * @param latLng Latitude and Longitude of a geofence.
     * @return ParseGeoPoint
     */
    public static ParseGeoPoint toParseGeoPoint(LatLng latLng) {
        return new ParseGeoPoint(latLng.latitude, latLng.longitude);
    }

    /**
     * @param timeString time string in HH:MM format
     * @return Calendar object of that hour and minute of today
     */
    public static Calendar timeStringToCalendar(String timeString) {
        if (!TextUtils.isEmpty(timeString)) {
            Calendar now = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            try {
                // parse time string
                cal.setTime(df.parse(timeString));
                // set date to today's date
                cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            return cal;
        }
        return null;
    }

    /**
     * @param cal Calendar object of certain hour and minute of any day
     * @return time string in HH:MM format
     */
    public static String calendarToTimeString(Calendar cal) {
        return df.format(cal.getTime());
    }

    /**
     * Convert Date to Calendar object, null if date is null
     *
     * @param date Date object
     * @return Calender object
     */
    public static Calendar dateToCalendar(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } else {
            return null;
        }
    }
}
