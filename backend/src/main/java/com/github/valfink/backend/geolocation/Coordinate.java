package com.github.valfink.backend.geolocation;

import java.math.BigDecimal;

public record Coordinate(
        BigDecimal latitude,
        BigDecimal longitude
) {
}
