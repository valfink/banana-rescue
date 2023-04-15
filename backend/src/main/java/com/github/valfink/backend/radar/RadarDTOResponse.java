package com.github.valfink.backend.radar;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.geolocation.Coordinate;

import java.util.List;

public record RadarDTOResponse(
        Coordinate center,
        int radiusInMeters,
        List<FoodItemDTOResponse> foodItems
) {
}
