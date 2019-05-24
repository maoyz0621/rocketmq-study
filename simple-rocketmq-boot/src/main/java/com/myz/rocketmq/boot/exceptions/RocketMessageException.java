package com.myz.rocketmq.boot.exceptions;

public class RocketMessageException extends RuntimeException {

    public RocketMessageException() {
    }

    public RocketMessageException(String message) {
        super(message);
    }

    public RocketMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketMessageException(Throwable cause) {
        super(cause);
    }

    public RocketMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
