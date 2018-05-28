package com.example.docto.googlemapapplication;

import android.location.Location;

public class RestPlace {

    private final String name;

    private  Location location;
    private boolean isOpened;
    private float rating;


    public RestPlace(String name) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }
}
