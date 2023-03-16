package com.github.valfink.backend.fooditem;

import java.time.Instant;

public record FoodItem(
        String id,
        String title,
        String photo_uri,
        String location,
        Instant pickup_until,
        Instant consume_until,
        String description
) {
}
