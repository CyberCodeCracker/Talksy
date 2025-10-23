package com.amouri_dev.talksy.entities.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    private Long id;
    private String name;
    private long unreadChatsCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean isRecipientOnline;
    private Long senderId;
    private Long recipientId;
    private String recipientProfilePicture;

}
