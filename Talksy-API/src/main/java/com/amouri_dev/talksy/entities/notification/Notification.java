package com.amouri_dev.talksy.entities.notification;

import com.amouri_dev.talksy.entities.message.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private Long chatId;
    private String content;
    private Long senderId;
    private Long recipientId;
    private String chatName;
    private MessageType messageType;
    private NotificationType type;
    private byte[] media;
}
