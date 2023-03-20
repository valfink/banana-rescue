package com.github.valfink.backend.mongouser;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class MongoUserController {
    private final MongoUserService mongoUserService;

    @PostMapping
    public MongoUserDTOResponse signUp(@RequestBody MongoUserDTORequest mongoUserDTORequest) {
        return mongoUserService.signUp(mongoUserDTORequest);
    }

    @PostMapping("/login")
    public MongoUserDTOResponse login(Principal principal) {
        return getMe(principal);
    }

    @GetMapping("/me")
    public MongoUserDTOResponse getMe(Principal principal) {
        return mongoUserService.getMe(principal);
    }
}
