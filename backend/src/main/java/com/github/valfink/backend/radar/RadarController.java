package com.github.valfink.backend.radar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/my-radar")
@RequiredArgsConstructor
public class RadarController {
    private final RadarService radarService;

    @PostMapping
    public RadarDTOResponse addMyRadar(@RequestBody RadarDTORequest radarDTORequest, Principal principal) {
        return radarService.addMyRadar(radarDTORequest, principal);
    }

    @GetMapping
    public RadarDTOResponse getMyRadar(Principal principal) {
        return radarService.getMyRadar(principal);
    }
}
