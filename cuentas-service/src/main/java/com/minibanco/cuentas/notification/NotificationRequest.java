package com.minibanco.cuentas.notification;

public record NotificationRequest(Long transferId, String message) {
}
