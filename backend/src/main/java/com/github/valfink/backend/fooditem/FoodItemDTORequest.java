package com.github.valfink.backend.fooditem;

import java.time.Instant;

public record FoodItemDTORequest(
        String title,
        String location,
        Instant pickup_until,
        Instant consume_until,
        String description
) {
}
