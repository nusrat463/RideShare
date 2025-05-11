package com.example.RiderService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue rideCreatedQueue() {
        return new Queue("ride.created.queue", true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("ride.exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(rideCreatedQueue()).to(exchange()).with("ride.created");
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
