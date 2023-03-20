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

    @GetMapping("/me")
    public MongoUserDTOResponse getMe(Principal principal) {
        return mongoUserService.getMe(principal);
    }
}
