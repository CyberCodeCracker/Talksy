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
    public ChatResponse createChat(Long senderId, Long recipientId) { // Changed: Return ChatResponse
        log.info("Creating chat between senderId={} and recipientId={}", senderId, recipientId);

        // Check for existing chat
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(senderId, recipientId);
        if (existingChat.isPresent()) {
            log.info("Existing chat found: {}", existingChat.get().getId());
            return mapper.toChatResponse(existingChat.get(), senderId);
        }

        try {
            User sender = this.findUserById(senderId);
            User recipient = this.findUserById(recipientId);
            log.debug("Found users: sender={}, recipient={}", sender.getEmail(), recipient.getEmail());

            Chat chat = new Chat();
            chat.setSender(sender);
            chat.setRecipient(recipient);

            Chat savedChat = chatRepository.save(chat);
            log.info("Created new chat with ID: {}", savedChat.getId());

            return mapper.toChatResponse(savedChat, senderId);
        } catch (Exception e) {
            log.error("Error creating chat: {}", e.getMessage(), e);
            throw e; // Re-throw to get 500 with details
        }
    }

    private User findUserById(Long userId) {
        log.debug("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}