package com.example.RiderService.controller;

import com.example.RiderService.model.RiderLocationUpdate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LocationBroadcastController {

    private final SimpMessagingTemplate messagingTemplate;

    public LocationBroadcastController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendLocationUpdate(RiderLocationUpdate update) {
        messagingTemplate.convertAndSend("/topic/ride." + update.getRideId(), update);
    }
}

