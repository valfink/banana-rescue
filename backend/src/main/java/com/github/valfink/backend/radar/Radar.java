package com.github.valfink.backend.radar;

import com.github.valfink.backend.geolocation.Coordinate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("radars")
public record Radar(
        @Id
        String userId,
        Coordinate center,
        int radiusInMeters
) {
}
