package com.amouri_dev.talksy.controllers;

import com.amouri_dev.talksy.core.Iservices.IMessageService;
import com.amouri_dev.talksy.entities.message.MessageRequest;
import com.amouri_dev.talksy.entities.message.MessageResponse;
import com.amouri_dev.talksy.entities.message.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final IMessageService messageService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/save-message")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(
            @RequestBody @Valid MessageRequest message
    ) {
        this.messageService.saveMessage(message);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(
            @RequestParam("chat-id") Long chatId,
            @Parameter()
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        this.messageService.uploadMediaMessage(chatId, file, authentication);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setMessagesToSeen(
            @RequestParam("chat-id") Long chatId,
            Authentication authentication
    ) {
        this.messageService.setMessagesToSeen(chatId, authentication);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping("/{chat-id}/{message-id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void editMessage(
            @PathVariable("chat-id") Long chatId,
            @PathVariable("message-id") Long messageId,
            @RequestBody @Valid MessageUpdateRequest request,
            Authentication authentication
            ) {
        this.messageService.editMessage(chatId, messageId, authentication, request);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/chat/{chat-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getMessages(
            @PathVariable("chat-id") Long chatId
    ) {
        return this.messageService.getAllMessages(chatId);
    }

}
