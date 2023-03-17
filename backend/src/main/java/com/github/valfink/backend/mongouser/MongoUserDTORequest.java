package com.github.valfink.backend.mongouser;

public record MongoUserDTORequest(
        String username,
        String password
) {
}
