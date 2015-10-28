package io.github.sangchankim.exceptions;

public class InstagramAPIException extends Exception {

    public InstagramAPIException(String message) {
        super(message);
    }

    public InstagramAPIException(String message, Throwable t) {
        super(message, t);
    }
}
