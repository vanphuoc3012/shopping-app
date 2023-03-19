package com.ecommerce.site.shop.rabbitmq;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public TopicExchange emailTopicExchange(@Value("${amqp.exchange.email}") String topicName) {
        return ExchangeBuilder.topicExchange(topicName).durable(true).build();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new  Jackson2JsonMessageConverter();
    }
}
