package com.example.RiderService.rabbitMq;
import com.example.RiderService.model.Ride;
import com.example.RiderService.model.RideAssignedEvent;
import com.example.RiderService.model.Rider;
import com.example.RiderService.model.RiderNotificationRequest;
import com.example.RiderService.repository.RideRepository;
import com.example.RiderService.repository.RiderRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RideMessageConsumer {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WebSocketNotifier webSocketNotifier;

    private final Map<Long, CompletableFuture<Boolean>> riderResponseMap = new ConcurrentHashMap<>();

    public Map<Long, CompletableFuture<Boolean>> getRiderResponseMap() {
        return riderResponseMap;
    }

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
        int attempt = 0;
        for (Rider rider : availableRiders) {
            if (attempt >= 3) {
                rabbitTemplate.convertAndSend("ride.exchange", "ride.match.dlq", rideId);
                return;
            }

            boolean notified = notifyRider(rider.getId(), ride);
            if (!notified) {
                attempt++;
                continue;
            }

            boolean accepted = awaitRiderResponse(rideId, rider.getId());
            if (accepted) {
                ride.setRiderId(rider.getId());
                ride.setStatus("ASSIGNED");
                rideRepository.save(ride);

                rider.setAvailable(false);
                riderRepository.save(rider);

                RideAssignedEvent assignedEvent = new RideAssignedEvent(ride.getId(), rider.getId(), ride.getUserId());
                rabbitTemplate.convertAndSend("ride.exchange", "ride.assigned", assignedEvent);
                return;
            }

            attempt++;
        }

        rabbitTemplate.convertAndSend("ride.exchange", "ride.match.retry.delayed", rideId);
    }


    private boolean notifyRider(String riderId, Ride ride) {
        RiderNotificationRequest request = new RiderNotificationRequest(
                ride.getId(),
                ride.getUserId(),
                ride.getPickupLocation(),
                ride.getDropoffLocation()
        );

        try {
            webSocketNotifier.notifyRider(riderId, request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean awaitRiderResponse(Long rideId, String riderId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        riderResponseMap.put(rideId, future); // shared map to store pending responses

        try {
            return future.get(15, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return false; // Timeout, treat as rejection
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            riderResponseMap.remove(rideId);
        }
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
