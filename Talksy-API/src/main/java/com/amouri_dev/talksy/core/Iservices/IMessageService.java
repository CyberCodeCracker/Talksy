package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.message.MessageRequest;
import com.amouri_dev.talksy.entities.message.MessageResponse;
import com.amouri_dev.talksy.entities.message.MessageUpdateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMessageService {

    void saveMessage(MessageRequest request);
    List<MessageResponse> getAllMessages(Long chatId);
    void setMessagesToSeen(Long chatId, Authentication auth);
    void uploadMediaMessage(Long chatId, MultipartFile file, Authentication auth);
    void editMessage(Long messageId, Long chatId, Authentication auth, MessageUpdateRequest request);
    void deleteMessage(Long messageId, Authentication auth);
}
