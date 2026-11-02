package com.minibanco.notificaciones.notification;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();

    public Notification save(Notification notification){
        notifications.add(notification);

        return notification;
    }

    public List<Notification> findAll() {
        return notifications;
    }
}
