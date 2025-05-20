package com.example.ride_sharing.config;

import com.example.ride_sharing.rabbitmq.RideMessagePublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RIDE_QUEUE = "rideQueue";
    public static final String RIDE_EXCHANGE = "rideExchange";
    public static final String ROUTING_KEY = "ride.created";


    @Bean
    public Queue rideQueue() {
        return new Queue(RIDE_QUEUE, false);
    }

    @Bean
    public DirectExchange rideExchange() {
        return new DirectExchange(RIDE_EXCHANGE);
    }

    @Bean
    public Binding rideBinding(Queue rideQueue, DirectExchange rideExchange) {
        return BindingBuilder
                .bind(rideQueue)
                .to(rideExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue rideAcceptedQueue() {
        return new Queue("ride.assigned", false);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(rideAcceptedQueue()).to(rideExchange()).with("ride.assigned");
    }



}
