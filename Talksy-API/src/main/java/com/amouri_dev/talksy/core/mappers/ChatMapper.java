package com.amouri_dev.talksy.core.mappers;

import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.entities.chat.ChatResponse;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class ChatMapper {
    public ChatResponse toChatResponse(Chat chat, Long senderId) {
        if (chat == null) {
            return null;
        }
        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(senderId))
                .unreadChatsCount(chat.getUnreadMessagesCount(senderId))
                .lastMessage(chat.getLastMessage())
                .isRecipientOnline(chat.getRecipient().isUserOnline())
                .senderId(senderId)
                .recipientId(chat.getRecipient().getId())
                .lastMessageTime(chat.getLastMessageTime())
                .build();
    }
}
