package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IChatService;
import com.amouri_dev.talksy.core.mappers.ChatMapper;
import com.amouri_dev.talksy.entities.chat.Chat;
import com.amouri_dev.talksy.entities.chat.ChatResponse;
import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.infrastructure.ChatRepository;
import com.amouri_dev.talksy.infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService implements IChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByRecipientID(Authentication auth) {
        final User user = ((User) auth.getPrincipal());
        final Long userId = user.getId();
        return chatRepository.findChatsBySenderId(user.getId())
                .stream()
                .map(chat -> mapper.toChatResponse(chat, userId))
                .toList()
                ;
    }

    @Override
    @Transactional
    public Long createChat(Long senderId, Long recipientId) {
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(senderId, recipientId);
        if (existingChat.isPresent()) {
            return existingChat.get().getId();
        }

        User sender = this.findUserById(senderId);
        User recipient = this.findUserById(recipientId);

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(recipient);

        return chatRepository.save(chat).getId();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}
