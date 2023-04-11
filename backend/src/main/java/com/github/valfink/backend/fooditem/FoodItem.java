package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.geolocation.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("foodItems")
public record FoodItem(
        @Id
        String id,
        String donatorId,
        String title,
        String photoUri,
        Location location,
        Instant pickupUntil,
        Instant consumeUntil,
        String description
) {
}
