package com.github.valfink.backend.radar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RadarService {
    private final RadarRepository radarRepository;
}
