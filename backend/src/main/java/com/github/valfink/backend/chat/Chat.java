package com.github.valfink.backend.chat;

public record Chat(
        String id,
        String donatorId,
        String candidateId,
        String foodItemId
) {
}

