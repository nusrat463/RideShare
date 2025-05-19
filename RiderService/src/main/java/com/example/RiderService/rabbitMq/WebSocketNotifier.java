package com.example.RiderService.rabbitMq;

import com.example.RiderService.model.RiderNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotifier {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyRider(String riderId, RiderNotificationRequest request) {
        messagingTemplate.convertAndSend("/topic/rider/" + riderId, request);
    }
}

