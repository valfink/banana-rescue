package com.github.valfink.backend.mongouser;

import com.github.valfink.backend.util.IdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MongoUserServiceTest {
    MongoUserService mongoUserService;
    MongoUserRepository mongoUserRepository;
    IdService idService;
    PasswordEncoder passwordEncoder;
    MongoUser mongoUser1;
    MongoUserDTOResponse mongoUserDTOResponse1;

    @BeforeEach
    void setUp() {
        mongoUserRepository = mock(MongoUserRepository.class);
        idService = mock(IdService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mongoUserService = new MongoUserService(mongoUserRepository, idService, passwordEncoder);

        mongoUser1 = new MongoUser("1", "user", "pass", "BASIC");
        mongoUserDTOResponse1 = new MongoUserDTOResponse(mongoUser1.id(), mongoUser1.username());
    }

    @Test
    void signUp_whenUsernameAndPasswordOk_thenReturnNewUser() {
        // GIVEN
        when(mongoUserRepository.existsByUsername(mongoUser1.username())).thenReturn(false);
        when(passwordEncoder.encode(mongoUser1.password())).thenReturn(mongoUser1.password());
        when(idService.generateId()).thenReturn(mongoUser1.id());
        when(mongoUserRepository.save(mongoUser1)).thenReturn(mongoUser1);

        // WHEN
        MongoUserDTOResponse expected = mongoUserDTOResponse1;
        MongoUserDTOResponse actual = mongoUserService.signUp(new MongoUserDTORequest(mongoUser1.username(), mongoUser1.password()));

        // THEN
        assertEquals(expected, actual);

    }

    @Test
    void signUp_whenUsernameEmpty_thenThrowException() {
        // GIVEN
        MongoUserDTORequest invalidUser = new MongoUserDTORequest("", mongoUser1.password());

        // WHEN & THEN
        assertThrows(BadCredentialsException.class, () -> mongoUserService.signUp(invalidUser));
    }

    @Test
    void signUp_whenUserExistsAlready_thenThrowException() {
        // GIVEN
        when(mongoUserRepository.existsByUsername(mongoUser1.username())).thenReturn(true);
        MongoUserDTORequest existingUser = new MongoUserDTORequest(mongoUser1.username(), mongoUser1.password());

        // WHEN & THEN
        assertThrows(BadCredentialsException.class, () -> mongoUserService.signUp(existingUser));
    }
}
