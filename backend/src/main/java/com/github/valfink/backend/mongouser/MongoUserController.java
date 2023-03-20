package com.github.valfink.backend.mongouser;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class MongoUserController {
    private final MongoUserService mongoUserService;

    @PostMapping
    public MongoUserDTOResponse signUp(@RequestBody MongoUserDTORequest mongoUserDTORequest) {
        return mongoUserService.signUp(mongoUserDTORequest);
    }
}
