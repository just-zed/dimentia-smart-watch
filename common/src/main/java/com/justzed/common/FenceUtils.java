package com.justzed.common;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.Calendar;
import java.util.Date;

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

//    private static final String TIME_FORMATTER = "HH:MM";
//    private static final DateFormat df = new SimpleDateFormat(TIME_FORMATTER, Locale.ENGLISH);

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
