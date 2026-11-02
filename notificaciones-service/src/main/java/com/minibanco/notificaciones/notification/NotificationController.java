package com.minibanco.notificaciones.notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        Notification saved = notificationService.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    @GetMapping
    public List<Notification> findAll(){
        return notificationService.findAll();
    }
}
