package com.example.RiderService.rabbitMq;

import com.example.RiderService.model.Ride;
import com.example.RiderService.repository.RideRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RideMessageConsumer {

    private final RideRepository rideRepository;

    public RideMessageConsumer(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @RabbitListener(queues = "rideQueue")
    public void consumeRideCreatedMessage(Ride ride) {
        try {
            Ride rideSave = new Ride();
            rideSave.setId("1"); // ✅ generate a new ID here
            rideSave.setUserId(ride.getUserId());
            rideSave.setPickupLocation(ride.getPickupLocation());
            rideSave.setDropoffLocation(ride.getDropoffLocation());
            rideSave.setStatus("ASSIGNED");
            rideRepository.save(rideSave);

            System.out.println("Ride assigned to rider: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String assignRider() {
        // Simulated rider assignment logic (use UUID or hardcoded ID)
        return UUID.randomUUID().toString();
    }
}
