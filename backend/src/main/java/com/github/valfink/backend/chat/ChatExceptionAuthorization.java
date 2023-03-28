package com.github.valfink.backend.chat;

public class ChatExceptionAuthorization extends RuntimeException {
    public ChatExceptionAuthorization(String message) {
        super(message);
    }
}
