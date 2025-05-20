package com.example.RiderService.rabbitMq;

import com.example.RiderService.model.RiderLocationUpdate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LocationUpdateListener {

    private final WebSocketBroadcaster broadcaster; // Custom component to handle WebSocket messaging

    public LocationUpdateListener(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @RabbitListener(queues = "ride.location.update.queue")
    public void handleLocationUpdate(RiderLocationUpdate update) {
        broadcaster.sendLocationUpdate(update);
    }
}
