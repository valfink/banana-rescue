package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.util.List;

public record ChatDTOResponse(
        String id,
        MongoUserDTOResponse donator,
        MongoUserDTOResponse candidate,
        FoodItemDTOResponse foodItem,
        List<ChatMessage> messages
) {
}
