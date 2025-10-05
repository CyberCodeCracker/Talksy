package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.notification.Notification;

public interface INotificationService {
    void sendNotification(Long userId, Notification notification);
}
