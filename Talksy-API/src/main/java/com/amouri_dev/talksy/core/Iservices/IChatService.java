package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.chat.ChatResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IChatService {

    List<ChatResponse> getChatsByRecipientID(Authentication auth);
    Long createChat(Long senderId, Long recipientId);
}
