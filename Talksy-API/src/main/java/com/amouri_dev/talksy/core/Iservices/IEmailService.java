package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface IEmailService {
    @Async
    void sendEmail(String to,
                   String username,
                   EmailTemplateName emailTemplateName,
                   String confirmationUrl,
                   String activationCode,
                   String subject) throws MessagingException;
}
