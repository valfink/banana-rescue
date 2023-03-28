package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.util.List;

public record ChatDTOResponse(
        String id,
        FoodItemDTOResponse foodItem,
        MongoUserDTOResponse candidate,
        List<ChatMessage> messages
) {
}
