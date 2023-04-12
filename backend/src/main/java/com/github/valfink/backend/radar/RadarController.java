package com.github.valfink.backend.radar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/my-radar")
@RequiredArgsConstructor
public class RadarController {
    private final RadarService radarService;

    @PostMapping
    public RadarDTOResponse addRadar(@RequestBody RadarDTORequest radarDTORequest, Principal principal) {
        return radarService.addRadar(radarDTORequest, principal);
    }
}
