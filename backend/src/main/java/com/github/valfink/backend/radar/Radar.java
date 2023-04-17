package com.github.valfink.backend.radar;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.geolocation.Coordinate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Document("radars")
public record Radar(
        @Id
        String userId,
        Coordinate center,
        int radiusInMeters
) {
    static int metersToCoordinateDivisionScale = 10;
    static BigDecimal metersToCoordinateDivisor = new BigDecimal("111111");

    public boolean containsFoodItem(FoodItemDTOResponse item) {
        BigDecimal radiusAsCoordinateOffset = new BigDecimal(radiusInMeters).divide(metersToCoordinateDivisor, metersToCoordinateDivisionScale, RoundingMode.HALF_EVEN);

        return item.location().coordinate().latitude().subtract(center.latitude()).pow(2)
                .add(item.location().coordinate().longitude().subtract(center.longitude()).pow(2))
                .compareTo(radiusAsCoordinateOffset.pow(2))
                <= 0;
    }

    public RadarDTOResponse convertToDTOResponse(List<FoodItemDTOResponse> withFoodItems) {
        return new RadarDTOResponse(
                this.center,
                this.radiusInMeters,
                withFoodItems
        );
    }
}
