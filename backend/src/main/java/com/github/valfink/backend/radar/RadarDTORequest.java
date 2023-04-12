package com.github.valfink.backend.radar;

import com.github.valfink.backend.geolocation.Coordinate;

public record RadarDTORequest(
        Coordinate center,
        int radiusInMeters
) {
    public Radar convertToRadar(String withUserId) {
        return new Radar(
                withUserId,
                this.center,
                this.radiusInMeters
        );
    }
}
