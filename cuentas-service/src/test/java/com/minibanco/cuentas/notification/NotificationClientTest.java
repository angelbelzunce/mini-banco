package com.minibanco.cuentas.notification;

import com.minibanco.cuentas.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class NotificationClientTest {

    @Autowired
    NotificationClient notificationClient;

    @Test
    void send_whenNotificationServiceIsDown_doesNotThrow(){
        NotificationRequest request = new NotificationRequest(10L, "Transfer completed");

        assertDoesNotThrow(() -> notificationClient.send(request));
    }
}
