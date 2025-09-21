package com.amouri_dev.talksy.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Errorcode errorCode;
    private final Object[] args;

    public BusinessException(final Errorcode errorCode, final Object... args) {
        super(getFormattedMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String getFormattedMessage(Errorcode errorCode, Object[] args) {
        if (args != null && args.length > 0) {
            return String.format(errorCode.getDefaultMessage(), args);
        }
        return errorCode.getDefaultMessage();
    }
}
