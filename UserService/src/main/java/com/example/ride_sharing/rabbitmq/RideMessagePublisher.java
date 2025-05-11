package com.example.ride_sharing.rabbitmq;
import com.example.ride_sharing.config.RabbitMQConfig;
import com.example.ride_sharing.model.RideRequest;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RideMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public RideMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishRideCreatedEvent(RideRequest ride) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RIDE_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                ride
        );
    }
}