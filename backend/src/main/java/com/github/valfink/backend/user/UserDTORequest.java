package com.github.valfink.backend.user;

public record UserDTORequest(
        String username,
        String password
) {
}
