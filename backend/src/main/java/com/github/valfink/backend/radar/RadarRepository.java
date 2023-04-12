package com.github.valfink.backend.radar;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadarRepository extends MongoRepository<Radar, String> {
    boolean existsByUserId(String userId);
}
