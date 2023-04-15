package com.github.valfink.backend.radar;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.geolocation.Coordinate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("radars")
public record Radar(
        @Id
        String userId,
        Coordinate center,
        int radiusInMeters
) {
        public RadarDTOResponse convertToDTOResponse(List<FoodItemDTOResponse> withFoodItems) {
                return new RadarDTOResponse(
                        this.center,
                        this.radiusInMeters,
                        withFoodItems
                );
        }
}
