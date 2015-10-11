package com.justzed.common;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

/**
 * Tests for all Utility classes
 *
 * @author Freeman Man
 * @since 2015-10-11
 */
@RunWith(AndroidJUnit4.class)

public class UtilsTest extends TestCase {
    @Test
    public void testToLatLng() throws ParseException {
        double lat = 27.0f;
        double lng = 153.0f;

        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat, lng);

        LatLng latLng = FenceUtils.toLatLng(parseGeoPoint);

        assertEquals(latLng.latitude, lat);
        assertEquals(latLng.longitude, lng);

    }

    @Test
    public void testToParseGeoPoint() {
        double lat = 27.0f;
        double lng = 153.0f;

        LatLng latLng = new LatLng(lat, lng);

        ParseGeoPoint parseGeoPoint = FenceUtils.toParseGeoPoint(latLng);

        assertEquals(parseGeoPoint.getLatitude(), lat);
        assertEquals(parseGeoPoint.getLongitude(), lng);
    }


    @Test
    public void testDateToCalendar() {

        long timeInMillis = 1234567;

        Date date = new Date(timeInMillis);

        Calendar calendar = FenceUtils.dateToCalendar(date);

        assertEquals(calendar.getTimeInMillis(), timeInMillis);
    }
}
