package com.amouri_dev.talksy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum Errorcode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", HttpStatus.NOT_FOUND),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Invalid current password" , HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account already deactivated", HttpStatus.CONFLICT ),
    ACCOUNT_ALREADY_ACTIVATED("ACCOUNT_ALREADY_ACTIVATED", "Account already activated" , HttpStatus.CONFLICT ),;
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    Errorcode(final String code,
              final String defaultMessage,
              final HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
