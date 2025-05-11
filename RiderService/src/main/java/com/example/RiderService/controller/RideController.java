package com.example.RiderService.controller;

import com.example.RiderService.model.Ride;
import com.example.RiderService.service.RideService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rider")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/ride/{rideId}")
    public Ride getRide(@PathVariable String rideId) {
        return rideService.getRide(rideId);
    }
}