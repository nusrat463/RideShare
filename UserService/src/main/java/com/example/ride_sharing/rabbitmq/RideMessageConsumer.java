package com.example.ride_sharing.rabbitmq;

import com.example.ride_sharing.model.RideAssignedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RideMessageConsumer {

    @Autowired WebSocketNotifier webSocketNotifier;

    @RabbitListener(queues = "ride.assigned")
    public void rideAssigned(RideAssignedEvent assignedEvent) {
        webSocketNotifier.notifyUser(assignedEvent);
    }

}
