package com.example.RiderService.service;

import com.example.RiderService.model.Ride;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RideService {

    private final ConcurrentHashMap<String, Ride> rides = new ConcurrentHashMap<>();

    public void acceptRide(Ride event) {
        Ride ride = new Ride();
        ride.setId(event.getId());
        ride.setPickupLocation(event.getPickupLocation());
        ride.setDropoffLocation(event.getDropoffLocation());
        ride.setStatus("ACCEPTED");

        rides.put(ride.getId(), ride);
        System.out.println("Ride accepted and status updated.");
    }

    public Ride getRide(String rideId) {
        return rides.get(rideId);
    }
}
