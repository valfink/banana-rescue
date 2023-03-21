package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.time.Instant;

public record FoodItemDTOResponse(
        String id,
        MongoUserDTOResponse donator,
        String title,
        String photoUri,
        String location,
        Instant pickupUntil,
        Instant consumeUntil,
        String description
) {
}
