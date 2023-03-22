package com.github.valfink.backend.fooditem;

import java.time.Instant;

public record FoodItemDTORequest(
        String title,
        String location,
        Instant pickupUntil,
        Instant consumeUntil,
        String description
) {
}
