package com.amouri_dev.talksy.entities.message;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private Long senderId;
    private Long receiverId;
    private MessageType messageType;
    private Long chatId;

}
