package com.justzed.caretaker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nam Cuong on 7/09/2015.
 */

public class FenceCircle {

    LatLng latLng;
    double radius;

    public FenceCircle(LatLng latLng, double radius){
        super();
        this.latLng = latLng;
        this.radius = radius;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
