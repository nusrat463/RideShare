package com.example.RiderService.rabbitMq;

import com.example.RiderService.model.RiderLocationUpdate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, List<WebSocketSession>> rideSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Client should send a message like {"subscribeTo": 123}
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(message.getPayload());
        Long rideId = json.get("subscribeTo").asLong();
        rideSubscriptions.computeIfAbsent(rideId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    public void broadcastLocation(RiderLocationUpdate update) throws IOException {
        List<WebSocketSession> sessions = rideSubscriptions.get(update.getRideId());
        if (sessions != null) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(update);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        }
    }
}

