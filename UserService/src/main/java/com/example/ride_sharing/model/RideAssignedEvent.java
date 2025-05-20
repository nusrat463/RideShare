package com.example.ride_sharing.model;

public class RideAssignedEvent {
    private Long rideId;
    private Long riderId;
    private String userId;

    public RideAssignedEvent(Long id, String riderId, String userId) {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

