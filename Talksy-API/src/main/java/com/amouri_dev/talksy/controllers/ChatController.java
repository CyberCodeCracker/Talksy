package com.amouri_dev.talksy.controllers;

import com.amouri_dev.talksy.core.Iservices.IChatService;
import com.amouri_dev.talksy.entities.chat.ChatResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@Tag(name = "Chat", description = "Chat API")
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create-chat")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ChatResponse createChat( // Changed: Return ChatResponse
                                    @RequestParam(name = "sender-id") final Long senderId,
                                    @RequestParam(name = "recipient-id") final Long recipientId
    ) {
        return this.chatService.createChat(senderId, recipientId); // Now returns ChatResponse
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/get-chats")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ChatResponse> getAllChatsByRecipientId(Authentication authentication) {
        return this.chatService.getChatsByRecipientID(authentication);
    }
}