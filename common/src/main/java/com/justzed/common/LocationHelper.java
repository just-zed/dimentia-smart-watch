package com.justzed.common;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

/**
 * Created by freeman on 9/5/15.
 * <p>
 * This class contains helper function for location related operations
 */
public class LocationHelper {

    /**
     * convert ParseGeoPoint to google LatLng
     *
     * @param geoPoint
     * @return LatLng
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
     * @param latLng
     * @return ParseGeoPoint
     */
    public static ParseGeoPoint toParseGeoPoint(LatLng latLng) {
        return new ParseGeoPoint(latLng.latitude, latLng.longitude);
    }
}
