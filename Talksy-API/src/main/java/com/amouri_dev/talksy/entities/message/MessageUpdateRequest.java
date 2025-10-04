package com.amouri_dev.talksy.entities.message;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MessageUpdateRequest {
    Long id;
    String content;
}
