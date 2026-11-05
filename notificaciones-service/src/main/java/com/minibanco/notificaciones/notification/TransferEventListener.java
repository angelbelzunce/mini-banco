package com.minibanco.notificaciones.notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferEventListener {
    private final NotificationService notificationService;

    public TransferEventListener(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "transfers")
    public void onTransfer(String message){
        Long transferId = Long.valueOf(message);
        notificationService.save(new Notification(transferId, "Transfer completed"));
    }
}
