package com.github.valfink.backend.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("chatMessages")
public record ChatMessage(
        @Id
        String id,
        String chatId,
        String senderId,
        Instant timestamp,
        String content
) {
}
