package com.github.valfink.backend.mongouser;

import com.github.valfink.backend.util.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoUserService implements UserDetailsService {
    static final MongoUser PLACEHOLDER_MONGO_USER = new MongoUser("DELETED", "(no user)", "", "");
    private final MongoUserRepository mongoUserRepository;
    private final IdService idService;
    private final PasswordEncoder passwordEncoder;

    private MongoUserDTOResponse mongoUserDTOResponseFromMongoUser(MongoUser mongoUser) {
        return new MongoUserDTOResponse(
                mongoUser.id(),
                mongoUser.username()
        );
    }

    public MongoUserDTOResponse signUp(MongoUserDTORequest mongoUserDTORequest) {
        if (mongoUserDTORequest.username() == null || mongoUserDTORequest.username().isBlank()) {
            throw new MongoUserExceptionBadInputData("Username is required");
        }
        if (mongoUserDTORequest.password() == null || mongoUserDTORequest.password().isBlank()) {
            throw new MongoUserExceptionBadInputData("Password is required");
        }
        if (mongoUserRepository.existsMongoUserByUsername(mongoUserDTORequest.username())) {
            throw new MongoUserExceptionBadInputData("Username is already taken");
        }

        MongoUser mongoUser = mongoUserRepository.save(new MongoUser(
                idService.generateId(),
                mongoUserDTORequest.username(),
                passwordEncoder.encode(mongoUserDTORequest.password()),
                "BASIC"
        ));

        return mongoUserDTOResponseFromMongoUser(mongoUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        MongoUser mongoUser = mongoUserRepository.findMongoUserByUsername(username)
                .orElseThrow(() -> new MongoUserExceptionNotFound(MongoUserExceptionNotFound.reasonUsername(username)));
        return new User(
                mongoUser.username(),
                mongoUser.password(),
                List.of(new SimpleGrantedAuthority(("ROLE_" + mongoUser.role())))
        );
    }

    public MongoUserDTOResponse getMe(Principal principal) {
        MongoUser mongoUser = mongoUserRepository.findMongoUserByUsername(principal.getName())
                .orElseThrow(() -> new MongoUserExceptionNotFound(MongoUserExceptionNotFound.reasonUsername(principal.getName())));
        return mongoUserDTOResponseFromMongoUser(mongoUser);
    }

    public MongoUserDTOResponse getMongoUserDTOResponseById(String id, boolean returnPlaceholderIfNotFound) {
        MongoUser mongoUser;
        if (returnPlaceholderIfNotFound) {
            mongoUser = mongoUserRepository.findById(id)
                    .orElse(PLACEHOLDER_MONGO_USER);
        } else {
            mongoUser = mongoUserRepository.findById(id)
                    .orElseThrow(() -> new MongoUserExceptionNotFound(MongoUserExceptionNotFound.reasonId(id)));
        }

        return mongoUserDTOResponseFromMongoUser(mongoUser);
    }

    public MongoUserDTOResponse getMongoUserDTOResponseById(String id) {
        return getMongoUserDTOResponseById(id, false);
    }

    public MongoUserDTOResponse getMongoUserDTOResponseByUsername(String username) {
        MongoUser mongoUser = mongoUserRepository.findMongoUserByUsername(username)
                .orElseThrow(() -> new MongoUserExceptionNotFound(MongoUserExceptionNotFound.reasonUsername(username)));
        return mongoUserDTOResponseFromMongoUser(mongoUser);
    }
}
