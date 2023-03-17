package com.github.valfink.backend.fooditem;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("foodItems")
public record FoodItem(
        @Id
        String id,
        String title,
        String photo_uri,
        String location,
        Instant pickup_until,
        Instant consume_until,
        String description
) {
}
