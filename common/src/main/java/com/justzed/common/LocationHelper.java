package com.justzed.common;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

/**
 * Created by freeman on 9/5/15.
 */
public class LocationHelper {

    public static LatLng toLatLng(ParseGeoPoint geoPoint) throws ParseException {
        if (geoPoint != null) {
            return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        } else {
            return null;
        }
    }

    public static ParseGeoPoint toParseGeoPoint(LatLng latLng) {
        return new ParseGeoPoint(latLng.latitude, latLng.longitude);
    }
}
