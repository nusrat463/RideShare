package com.example.RiderService.model;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
    private double latitude;
    private double longitude;

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
