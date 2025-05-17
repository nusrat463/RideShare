package com.example.RiderService.rabbitMq;
import com.example.RiderService.model.Ride;
import com.example.RiderService.model.RideAssignedEvent;
import com.example.RiderService.model.Rider;
import com.example.RiderService.repository.RideRepository;
import com.example.RiderService.repository.RiderRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RideMessageConsumer {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "rideQueue")
    public void consumeRideCreatedMessage(Ride ride) {
        try {
            Ride rideSave = new Ride();
            Long maxId = rideRepository.findMaxId();
            rideSave.setId(maxId + 1);
            rideSave.setUserId(ride.getUserId());
            rideSave.setPickupLocation(ride.getPickupLocation());
            rideSave.setDropoffLocation(ride.getDropoffLocation());
            rideSave.setStatus("PENDING");
            rideRepository.save(rideSave);

            matchRiderToRide(rideSave.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void matchRiderToRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));

        List<Rider> availableRiders = riderRepository.finAvailableRiders();

        Rider nearestRider = findNearestRider(availableRiders, ride.getPickupLocation().getLatitude(), ride.getPickupLocation().getLongitude());

        if (nearestRider == null) {
            rabbitTemplate.convertAndSend("ride.exchange", "ride.match.retry.delayed", ride.getId());
            return;
        }
        /////// if riders should do retry, cause there will be always a nearest rider even if they are available or not
        ////// and their availability is not checked here

        ride.setRiderId(nearestRider.getId());
        ride.setStatus("ASSIGNED");
        rideRepository.save(ride);

        nearestRider.setAvailable(false);
        riderRepository.save(nearestRider);

        RideAssignedEvent assignedEvent = new RideAssignedEvent(ride.getId(), ride.getRiderId(), ride.getUserId());
        rabbitTemplate.convertAndSend("ride.exchange", "ride.assigned", assignedEvent);
    }

    private Rider findNearestRider(List<Rider> availableRiders, double pickupLat, double pickupLng) {
        return availableRiders.stream()
                .min(Comparator.comparingDouble(r ->
                        calculateDistance(pickupLat, pickupLng, r.getLatitude(), r.getLongitude())
                ))
                .orElse(null);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    @RabbitListener(queues = "ride.match.retry.queue")
    public void retryMatching(Message message) {
        Long rideId = Long.parseLong(new String(message.getBody()));
        Integer retryCount = (Integer) message.getMessageProperties().getHeaders()
                .getOrDefault("x-retry-count", 0);

        if (retryCount >= 3) {
            System.out.println("Max retry reached for Ride ID: " + rideId + ". Sending to DLQ.");
            rabbitTemplate.convertAndSend("ride.exchange", "ride.match.dlq", rideId);
        } else {
            System.out.println("Retrying ride match for Ride ID: " + rideId + ", attempt #" + (retryCount + 1));

            MessageProperties props = new MessageProperties();
            props.setHeader("x-retry-count", retryCount + 1);

            Message newMessage =
                    new Message(String.valueOf(rideId).getBytes(), props);

            // Send to retry delayed queue again for next retry
            rabbitTemplate.send("ride.exchange", "ride.match.retry.delayed", newMessage);
        }
    }

    @RabbitListener(queues = "ride.match.dlq")
    public void handleFailedMatching(Long rideId) {
        System.err.println("🚨 Ride match permanently failed for ride ID: " + rideId);
        // can notify admin or save to DB
    }


}
