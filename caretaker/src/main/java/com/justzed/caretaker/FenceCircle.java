package com.justzed.caretaker;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

/**
 * Created by Nam Cuong on 7/09/2015.
 */

public class FenceCircle {

    GoogleMap map;
    private String title;
    private Circle circle;

    public FenceCircle(GoogleMap map, String title, Circle circle){
        super();
        this.map = map;
        this.title = title;
        this.circle = circle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        return circle;
    }

    public Circle drawCircle(){
        return map.addCircle(new CircleOptions()
                .center(circle.getCenter())
                .radius(circle.getRadius())
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
    }
}
