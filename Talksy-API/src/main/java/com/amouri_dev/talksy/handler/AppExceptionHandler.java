package com.amouri_dev.talksy.handler;

import com.amouri_dev.talksy.exception.BusinessException;
import com.amouri_dev.talksy.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler {

    private final View error;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        final ErrorResponse response = ErrorResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build()
                ;
        log.info("Business exception: {}", e.getMessage());
        log.debug(e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus() != null ?
                e.getErrorCode().getHttpStatus() :
                HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(final DisabledException e) {
        log.debug(e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.USER_DISABLED.getCode())
                .message(ErrorCode.USER_DISABLED.getDefaultMessage())
                .build()
                ;
        return ResponseEntity.status(ErrorCode.USER_DISABLED.getHttpStatus())
                .body(response)
                ;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(final BadCredentialsException e) {
        log.debug(e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.BAD_CREDENTIALS.getCode())
                .message(ErrorCode.BAD_CREDENTIALS.getDefaultMessage())
                .build()
                ;
        return ResponseEntity.status(ErrorCode.BAD_CREDENTIALS.getHttpStatus())
                .body(response)
                ;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(final UsernameNotFoundException e) {
        log.debug(e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.USERNAME_NOT_FOUND.getCode())
                .message(ErrorCode.USERNAME_NOT_FOUND.getDefaultMessage())
                .build()
                ;
        return ResponseEntity.status(ErrorCode.USERNAME_NOT_FOUND.getHttpStatus())
                .body(response)
                ;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.debug(e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.ENTITY_NOT_FOUND.getCode())
                .message(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage())
                .build()
                ;
        return ResponseEntity.status(ErrorCode.ENTITY_NOT_FOUND.getHttpStatus())
                .body(response)
                ;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String errorCode = error.getDefaultMessage();
                    errors.add(ErrorResponse.ValidationError.builder()
                                    .field(fieldName)
                                    .code(errorCode)
                                    .message(errorCode)
                                    .build())
                    ;
                });
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .validationErrors(errors)
                .build()
                ;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse)
                ;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("Unexpected exception", e.getMessage());
        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_EXCEPTION.getCode())
                .message(ErrorCode.INTERNAL_EXCEPTION.getDefaultMessage())
                .build()
                ;
        return ResponseEntity.status(ErrorCode.INTERNAL_EXCEPTION.getHttpStatus())
                .body(response)
                ;
    }
}
