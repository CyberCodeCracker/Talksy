package com.amouri_dev.talksy.controllers;

import com.amouri_dev.talksy.core.Iservices.IMessageService;
import com.amouri_dev.talksy.entities.message.MessageRequest;
import com.amouri_dev.talksy.entities.message.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/save-message")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(
            @RequestBody @Valid MessageRequest message
    ) {
        messageService.saveMessage(message);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(
            @RequestParam("chat-id") Long chatId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        messageService.uploadMediaMessage(chatId, file, authentication);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setMessagesToSeen(
            @RequestParam("chat-id") Long chatId,
            Authentication authentication
    ) {
        messageService.setMessagesToSeen(chatId, authentication);
    }

    @GetMapping("/chat/{chat-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getMessages(
            @PathVariable("chat-id") Long chatId
    ) {
        return messageService.getAllMessages(chatId);
    }

}
