package com.amouri_dev.talksy.entities.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private Long id;
    private String message;
    private MessageType type;
    private MessageState state;
    private Long senderId;
    private Long recipientId;
    private LocalDateTime createdAt;
    private byte[] media;
}
