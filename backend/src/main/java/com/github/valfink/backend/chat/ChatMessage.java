package com.github.valfink.backend.chat;

import java.time.Instant;

public record ChatMessage(
        String id,
        String chatId,
        String senderId,
        String receiverId,
        Instant timestamp,
        String content
) {
}
