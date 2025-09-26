package com.amouri_dev.talksy.handler;

import com.amouri_dev.talksy.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build()
                ;
        log.info("Business exception: {}", e.getMessage());
        log.debug(e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(body);
    }
}
