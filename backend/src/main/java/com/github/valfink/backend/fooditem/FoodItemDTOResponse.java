package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.time.Instant;

public record FoodItemDTOResponse(
        String id,
        MongoUserDTOResponse donator,
        String title,
        String photo_uri,
        String location,
        Instant pickup_until,
        Instant consume_until,
        String description
) {
}
