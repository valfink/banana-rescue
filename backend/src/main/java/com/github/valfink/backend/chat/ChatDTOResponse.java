package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record ChatDTOResponse(
        String id,
        FoodItemDTOResponse foodItem,
        MongoUserDTOResponse candidate,
        List<ChatMessage> messages
) {
    Instant getLastUpdate() {
        return messages.stream()
                .map(ChatMessage::timestamp)
                .max(Comparator.naturalOrder())
                .orElse(Instant.now());
    }
}

