package com.amouri_dev.talksy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", HttpStatus.NOT_FOUND),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Username not found" , HttpStatus.NOT_FOUND ),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Invalid current password" , HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account already deactivated", HttpStatus.CONFLICT ),
    ACCOUNT_ALREADY_ACTIVATED("ACCOUNT_ALREADY_ACTIVATED", "Account already activated" , HttpStatus.CONFLICT ),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT_ALREADY_EXISTS", "Account already exists", HttpStatus.BAD_REQUEST ),
    USER_DISABLED("ACCOUNT_DISABLED", "account disabled", HttpStatus.UNAUTHORIZED ),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Email and / or password is wrong" , HttpStatus.UNAUTHORIZED ),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal exception" , HttpStatus.INTERNAL_SERVER_ERROR ),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "Entity not found" , HttpStatus.NOT_FOUND ),
    INVALID_TOKEN("INVALID_TOKEN", "Invalid token" , HttpStatus.UNAUTHORIZED ),
    ;
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(final String code,
              final String defaultMessage,
              final HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
