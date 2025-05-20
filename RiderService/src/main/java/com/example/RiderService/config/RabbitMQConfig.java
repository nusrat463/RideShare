package com.example.RiderService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue rideCreatedQueue() {
        return new Queue("ride.created.queue", true);
    }

    @Bean
    public Queue rideQueue() {
        return new Queue("rideQueue", false);
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

    @Bean
    public Queue retryDelayedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "ride.exchange");
        args.put("x-dead-letter-routing-key", "ride.match.retry");
        args.put("x-message-ttl", 10000); // 10 seconds
        return new Queue("ride.match.retry.delayed", true, false, false, args);
    }


    @Bean
    public Binding retryDelayedBinding(Queue retryDelayedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(retryDelayedQueue).to(exchange).with("ride.match.retry.delayed");
    }


    @Bean
    public Queue retryQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "ride.exchange");
        args.put("x-dead-letter-routing-key", "ride.match.dlq");
        return new Queue("ride.match.retry.queue", true, false, false, args);
    }

    @Bean
    public Binding retryBinding(Queue retryQueue, DirectExchange exchange) {
        return BindingBuilder.bind(retryQueue).to(exchange).with("ride.match.retry");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("ride.match.dlq", true);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(exchange()).with("ride.match.dlq");
    }


    @Bean
    public TopicExchange locationExchange() {
        return new TopicExchange("location.update.exchange");
    }

    @Bean
    public Queue locationQueue() {
        return new Queue("ride.location.update.queue", true);
    }

    @Bean
    public Binding locationBinding() {
        return BindingBuilder
                .bind(locationQueue())
                .to(locationExchange())
                .with("ride.location.update.#");
    }
}
