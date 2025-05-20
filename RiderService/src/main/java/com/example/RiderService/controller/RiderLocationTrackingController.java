package com.example.RiderService.controller;

import com.example.RiderService.model.RiderLocationUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiderLocationTrackingController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final String exchange = "location.update.exchange";

    @PostMapping("/location")
    public ResponseEntity<String> receiveLocationUpdate(@RequestBody RiderLocationUpdate update) {
        String routingKey = "ride.location.update." + update.getRideId();
        rabbitTemplate.convertAndSend(exchange, routingKey, update);
        return ResponseEntity.ok("Location update published");
    }
}