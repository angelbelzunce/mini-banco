package com.minibanco.cuentas.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransferEventPublisher {
    private static final String TOPIC = "transfers";
    private static final Logger log = LoggerFactory.getLogger(TransferEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransferEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Long transferId) {

        try {
            kafkaTemplate.send(TOPIC, "Transfer " + transferId + " completed");

        } catch (Exception e) {
            log.warn("Could not publish event for transfer " + transferId);

        }
    }
}
