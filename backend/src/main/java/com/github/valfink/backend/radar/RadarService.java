package com.github.valfink.backend.radar;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class RadarService {
    private final RadarRepository radarRepository;
    private final MongoUserService mongoUserService;

    public RadarDTORequest addRadar(RadarDTORequest radarDTORequest, Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());

        if (radarRepository.existsByUserId(user.id())) {
            throw new RadarExceptionBadInputData("You can only set one radar at a time.");
        }
        if (radarDTORequest.center() == null || radarDTORequest.center().latitude() == null || radarDTORequest.center().longitude() == null) {
            throw new RadarExceptionBadInputData("Center coordinate must be set.");
        }

        Radar radar = radarRepository.save(new Radar(user.id(), radarDTORequest.center(), radarDTORequest.radiusInMeters()));

        return new RadarDTORequest(radar.center(), radar.radiusInMeters());
    }
}
