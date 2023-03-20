package com.github.valfink.backend.mongouser;

import com.github.valfink.backend.util.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoUserService implements UserDetailsService {
    private final MongoUserRepository mongoUserRepository;
    private final IdService idService;
    private final PasswordEncoder passwordEncoder;

    public MongoUserDTOResponse signUp(MongoUserDTORequest mongoUserDTORequest) {
        if (mongoUserDTORequest.username() == null || mongoUserDTORequest.username().length() == 0) {
            throw new BadCredentialsException("Username is required");
        }
        if (mongoUserDTORequest.password() == null || mongoUserDTORequest.password().length() == 0) {
            throw new BadCredentialsException("Password is required");
        }
        if (mongoUserRepository.existsMongoUserByUsername(mongoUserDTORequest.username())) {
            throw new BadCredentialsException("Username is already taken");
        }

        MongoUser savedUser = mongoUserRepository.save(new MongoUser(
                idService.generateId(),
                mongoUserDTORequest.username(),
                passwordEncoder.encode(mongoUserDTORequest.password()),
                "BASIC"
        ));

        return new MongoUserDTOResponse(
                savedUser.id(),
                savedUser.username()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        MongoUser mongoUser = mongoUserRepository.findMongoUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found!"));
        return new User(
                mongoUser.username(),
                mongoUser.password(),
                List.of(new SimpleGrantedAuthority(("ROLE_" + mongoUser.role())))
        );
    }

    public MongoUserDTOResponse getMe(Principal principal) {
        MongoUser mongoUser = mongoUserRepository.findMongoUserByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("The user " + principal.getName() + " doesn't exist in the database."));
        return new MongoUserDTOResponse(
                mongoUser.id(),
                mongoUser.username()
        );
    }
}
