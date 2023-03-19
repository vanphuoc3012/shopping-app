package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.dto.EmailDTO;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailService {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("${amqp.exchange.email}")
    private String emailExchangeTopic;
    @Value("${amqp.routingkey.email}")
    private String emailRoutingKey;

    public void sendEmail(EmailDTO emailDTO) {
        log.info("Publishing email event: sending email to {}", emailDTO.getToEmail());
        amqpTemplate.convertAndSend(emailExchangeTopic, emailRoutingKey, emailDTO);
    }
}
