package com.example.shoplogs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfiguration {

    @Bean
    public TopicExchange logsTopicExchange() {
        return ExchangeBuilder.topicExchange("logs.topic")
                .durable(true)
                .build();
    }

    @Bean
    public Queue logsQueue() {
        return QueueBuilder.durable("logs.queue").build();
    }

    @Bean
    public Binding logsBinding(final Queue logsQueue,
                               final TopicExchange logsExchange) {
        return BindingBuilder.bind(logsQueue)
                .to(logsExchange).with("#");
    }
}
