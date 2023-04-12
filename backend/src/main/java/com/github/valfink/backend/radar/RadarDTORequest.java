package com.github.valfink.backend.radar;

import com.github.valfink.backend.geolocation.Coordinate;

public record RadarDTORequest(
        Coordinate center,
        int radiusInMeters
) {
}
