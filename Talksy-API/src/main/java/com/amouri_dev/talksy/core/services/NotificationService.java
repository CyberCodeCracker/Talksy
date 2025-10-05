package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.INotificationService;
import com.amouri_dev.talksy.entities.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(Long userId, Notification notification) {
        log.info("Sending WS notification to {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/chat", notification);

    }
}
