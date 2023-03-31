package com.github.valfink.backend.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chats")
public record Chat(
        @Id
        String id,
        String foodItemId,
        String candidateId,
        String donatorId
) {
}
