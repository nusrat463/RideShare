package com.example.RiderService.controller;

import com.example.RiderService.model.Ride;
import com.example.RiderService.rabbitMq.RideMessageConsumer;
import com.example.RiderService.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/rider")
public class RiderController {

    @Autowired
    private RideMessageConsumer rideMessageConsumer;

    @PostMapping("/respond")
    public ResponseEntity<String> respondToRide(@RequestParam Long rideId, @RequestParam boolean accepted) {
        CompletableFuture<Boolean> future = rideMessageConsumer.getRiderResponseMap().get(rideId);

        if (future != null) {
            future.complete(accepted);  // Notify the consumer thread waiting on this
            return ResponseEntity.ok("Response received.");
        } else {
            return ResponseEntity.badRequest().body("No pending ride for given ID.");
        }
    }

}