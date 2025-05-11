package com.example.ride_sharing.service;

import com.example.ride_sharing.model.RideRequest;
import com.example.ride_sharing.rabbitmq.RideMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {


    @Autowired
    private RideMessagePublisher rideMessagePublisher;

    public RideRequest createRide(RideRequest ride) {
        // Save the ride in DB
        //RideRequest savedRide = rideRepository.save(ride);

        // Publish to RabbitMQ for other services to handle (e.g., notifications, analytics)
        rideMessagePublisher.publishRideCreatedEvent(ride);

        return ride;
    }

//    public Iterable<RideRequest> getAllRides() {
//        return rideRepository.findAll();
//    }
}
