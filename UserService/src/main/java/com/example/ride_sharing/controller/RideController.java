package com.example.ride_sharing.controller;
import com.example.ride_sharing.model.RideRequest;
import com.example.ride_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private UserService userService;

    // Create a new ride
    @PostMapping("/create")
    public RideRequest createRide(@RequestBody RideRequest ride) {
        return userService.createRide(ride);
    }

    // Fetch all rides (just an example)
    /*@GetMapping("/all")
    public Iterable<RideRequest> getAllRides() {
        return userService.getAllRides();
    }*/
}

