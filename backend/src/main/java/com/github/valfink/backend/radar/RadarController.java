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
    public RadarDTO addRadar(@RequestBody RadarDTO radarDTO, Principal principal) {
        return radarService.addRadar(radarDTO, principal);
    }
}
