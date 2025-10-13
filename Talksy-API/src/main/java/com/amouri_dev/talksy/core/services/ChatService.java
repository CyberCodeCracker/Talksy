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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService implements IChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByRecipientID(Authentication auth) {
        String email = auth.getName();
        log.debug("Fetching chats for user email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new EntityNotFoundException("User not found: " + email);
                });
        Long userId = user.getId();
        List<Chat> chats = chatRepository.findChatsBySenderId(userId);
        log.debug("Found {} chats for user: {}", chats.size(), email);

        // Handle empty chats
        if (chats == null || chats.isEmpty()) {
            log.info("No chats found for user: {}", email);
            return Collections.emptyList();
        }

        return chats.stream()
                .map(chat -> mapper.toChatResponse(chat, userId))
                .toList();
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