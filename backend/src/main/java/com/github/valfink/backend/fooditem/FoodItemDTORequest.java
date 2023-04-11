package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.geolocation.Location;

import java.time.Instant;

public record FoodItemDTORequest(
        String title,
        Location location,
        Instant pickupUntil,
        Instant consumeUntil,
        String description
) {
}
