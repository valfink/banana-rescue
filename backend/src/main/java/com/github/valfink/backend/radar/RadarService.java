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

    public RadarDTO addRadar(RadarDTO radarDTO, Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());

        if (radarRepository.existsByUserId(user.id())) {
            throw new RadarExceptionBadInputData("You can only set one radar at a time.");
        }
        if (radarDTO.center() == null || radarDTO.center().latitude() == null || radarDTO.center().longitude() == null) {
            throw new RadarExceptionBadInputData("Center coordinate must be set.");
        }

        Radar radar = radarRepository.save(new Radar(user.id(), radarDTO.center(), radarDTO.radiusInMeters()));

        return new RadarDTO(radar.center(), radar.radiusInMeters());
    }
}
