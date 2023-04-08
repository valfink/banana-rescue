package com.github.valfink.backend.radar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-radar")
@RequiredArgsConstructor
public class RadarController {
    private final RadarService radarService;
}
