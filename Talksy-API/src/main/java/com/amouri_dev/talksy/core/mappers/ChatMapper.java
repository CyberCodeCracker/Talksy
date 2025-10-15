package com.amouri_dev.talksy.core.mappers;

import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.entities.chat.ChatResponse;
import com.amouri_dev.talksy.entities.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatMapper {

    public ChatResponse toChatResponse(Chat chat, Long currentUserId) {
        User currentUser = currentUserId.equals(chat.getSender().getId()) ?
                chat.getSender() : chat.getRecipient();
        User otherUser = currentUserId.equals(chat.getSender().getId()) ?
                chat.getRecipient() : chat.getSender();

        return ChatResponse.builder()
                .id(chat.getId())
                .senderId(chat.getSender().getId())
                .recipientId(chat.getRecipient().getId())
                .name(currentUser.getFirstName() + " " + currentUser.getLastName())
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime() != null ?
                        LocalDateTime.parse(chat.getLastMessageTime().toString()) : null)
                .unreadChatsCount(0L)
                .build();
    }
}