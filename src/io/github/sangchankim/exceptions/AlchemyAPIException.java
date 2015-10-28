package io.github.sangchankim.exceptions;

public class AlchemyAPIException extends Exception {

    public AlchemyAPIException(String message) {
        super(message);
    }

    public AlchemyAPIException(String message, Throwable t) {
        super(message, t);
    }
}
