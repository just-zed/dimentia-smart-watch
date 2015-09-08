package com.justzed.caretaker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nam Cuong on 7/09/2015.
 */

public class FenceCircle {

    private String title;
    private LatLng latLng;
    private double radius;

    public FenceCircle(String title, LatLng latLng, double radius){
        super();
        this.title = title;
        this.latLng = latLng;
        this.radius = radius;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
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
