package com.github.valfink.backend.util;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimestampService {
    public Instant generateTimestamp() {
        return Instant.now();
    }
}
