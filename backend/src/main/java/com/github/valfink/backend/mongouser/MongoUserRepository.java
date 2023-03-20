package com.github.valfink.backend.mongouser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<MongoUser, String> {
    boolean existsMongoUserByUsername(String username);

    Optional<MongoUser> findMongoUserByUsername(String username);
}
