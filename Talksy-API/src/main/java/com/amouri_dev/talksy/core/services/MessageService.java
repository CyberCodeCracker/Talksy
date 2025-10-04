package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IFileService;
import com.amouri_dev.talksy.core.Iservices.IMessageService;
import com.amouri_dev.talksy.core.mappers.MessageMapper;
import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.entities.message.*;
import com.amouri_dev.talksy.infrastructure.ChatRepository;
import com.amouri_dev.talksy.infrastructure.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final IFileService fileService;

    @Override
    @Transactional
    public void saveMessage(MessageRequest request) {
        Chat chat = this.getChatById(request.getChatId());

        Message message = new Message();
        message.setChat(chat);
        message.setContent(request.getContent());
        message.setSenderId(request.getSenderId());
        message.setRecipientId(request.getReceiverId());
        message.setType(request.getMessageType());

        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getAllMessages(Long chatId) {
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(msg -> mapper.toMessageResponse(msg))
                .toList()
                ;
    }

    @Override
    @Transactional
    public void setMessagesToSeen(Long chatId, Authentication auth) {
        Chat chat = this.getChatById(chatId);

        final Long recipientId = getRecipientId(chat, auth);

        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);
    }

    @Override
    public void uploadMediaMessage(Long chatId, MultipartFile file, Authentication auth) {
        Chat chat = this.getChatById(chatId);

        final Long senderId = getSenderId(chat, auth);
        final Long recipientId = getRecipientId(chat, auth);

        final String filePath = fileService.saveFile(file, senderId);
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        message.setMediaPath(filePath);

        messageRepository.save(message);
    }

    @Override
    public void editMessage(Long messageId, Long chatId, Authentication auth, MessageUpdateRequest request) {
        Message message = this.getMessageById(messageId);
        message.setContent(request.getContent());
        this.messageRepository.save(message);
    }


    @Override
    public void deleteMessage(Long messageId, Authentication auth) {
        Message message = this.getMessageById(messageId);
        Chat chat = message.getChat();
        chat.getMessages().remove(message);
        this.messageRepository.deleteById(messageId);
    }

    private Long getSenderId(Chat chat, Authentication auth) {
        if (chat.getSender().equals(auth.getName())) {
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }


    private Long getRecipientId(Chat chat, Authentication auth) {
        if (chat.getSender().equals(auth.getName())) {
            return chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }

    private Message getMessageById(Long messageId) {
        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }

    private Chat getChatById(Long chatId) {
        return this.chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with ID: "+ chatId + " not found"));
    }


}
