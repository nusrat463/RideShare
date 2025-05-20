package com.example.RiderService.rabbitMq;

import com.example.RiderService.model.RiderLocationUpdate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebSocketBroadcaster {

    private final LocationWebSocketHandler handler;

    public WebSocketBroadcaster(LocationWebSocketHandler handler) {
        this.handler = handler;
    }

    public void sendLocationUpdate(RiderLocationUpdate update) {
        try {
            handler.broadcastLocation(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
