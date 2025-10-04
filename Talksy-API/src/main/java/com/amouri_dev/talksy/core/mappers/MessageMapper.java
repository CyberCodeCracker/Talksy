package com.amouri_dev.talksy.core.mappers;

import com.amouri_dev.talksy.entities.message.Message;
import com.amouri_dev.talksy.entities.message.MessageResponse;
import com.amouri_dev.talksy.utils.FileUtils;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class MessageMapper {

    public MessageResponse toMessageResponse(Message msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .state(msg.getState())
                .type(msg.getType())
                .createdAt(msg.getCreatedDate())
                .recipientId(msg.getRecipientId())
                .senderId(msg.getSenderId())
                .message(msg.getContent())
                .media(FileUtils.readFileFromLocation(msg.getMediaPath()))
                .build()
                ;
    }
}
