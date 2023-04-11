package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.geolocation.Location;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

import java.time.Instant;

public record FoodItemDTOResponse(
        String id,
        MongoUserDTOResponse donator,
        String title,
        String photoUri,
        Location location,
        Instant pickupUntil,
        Instant consumeUntil,
        String description
) {
}
