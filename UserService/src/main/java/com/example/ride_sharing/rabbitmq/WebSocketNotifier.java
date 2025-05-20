package com.example.ride_sharing.rabbitmq;

import com.example.ride_sharing.model.RideAssignedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotifier {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyUser(RideAssignedEvent assignedEvent) {
        messagingTemplate.convertAndSend("/topic/user/" + assignedEvent);
    }
}
