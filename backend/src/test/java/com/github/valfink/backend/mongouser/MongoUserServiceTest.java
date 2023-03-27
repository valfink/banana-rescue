package com.github.valfink.backend.mongouser;

import com.github.valfink.backend.util.IdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
    Principal principal;

    @BeforeEach
    void setUp() {
        mongoUserRepository = mock(MongoUserRepository.class);
        idService = mock(IdService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mongoUserService = new MongoUserService(mongoUserRepository, idService, passwordEncoder);
        principal = mock(Principal.class);

        mongoUser1 = new MongoUser("1", "user", "pass", "BASIC");
        mongoUserDTOResponse1 = new MongoUserDTOResponse(mongoUser1.id(), mongoUser1.username());
    }

    @Test
    void signUp_whenUsernameAndPasswordOk_thenReturnNewUser() {
        // GIVEN
        when(mongoUserRepository.existsMongoUserByUsername(mongoUser1.username())).thenReturn(false);
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
        assertThrows(MongoUserExceptionBadInputData.class, () -> mongoUserService.signUp(invalidUser));
    }

    @Test
    void signUp_whenPasswordEmpty_thenThrowException() {
        // GIVEN
        MongoUserDTORequest invalidUser = new MongoUserDTORequest(mongoUser1.username(), "");

        // WHEN & THEN
        assertThrows(MongoUserExceptionBadInputData.class, () -> mongoUserService.signUp(invalidUser));
    }

    @Test
    void signUp_whenUserExistsAlready_thenThrowException() {
        // GIVEN
        when(mongoUserRepository.existsMongoUserByUsername(mongoUser1.username())).thenReturn(true);
        MongoUserDTORequest existingUser = new MongoUserDTORequest(mongoUser1.username(), mongoUser1.password());

        // WHEN & THEN
        assertThrows(MongoUserExceptionBadInputData.class, () -> mongoUserService.signUp(existingUser));
    }

    @Test
    void loadUserByUsername_whenUsernameIsInRepo_thenReturnUser() {
        // GIVEN
        when(mongoUserRepository.findMongoUserByUsername(mongoUser1.username())).thenReturn(Optional.ofNullable(mongoUser1));

        // WHEN
        UserDetails expected = new User(mongoUser1.username(), mongoUser1.password(), List.of(new SimpleGrantedAuthority(("ROLE_" + mongoUser1.role()))));
        UserDetails actual = mongoUserService.loadUserByUsername(mongoUser1.username());

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsername_whenUsernameIsNotInRepo_thenThrowException() {
        // GIVEN
        when(mongoUserRepository.findMongoUserByUsername(mongoUser1.username())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(MongoUserExceptionNotFound.class, () -> mongoUserService.loadUserByUsername("invalid user"));
    }

    @Test
    void getMe_whenPrincipalUsernameIsInRepo_thenReturnUser() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUser1.username());
        when(mongoUserRepository.findMongoUserByUsername(mongoUser1.username())).thenReturn(Optional.ofNullable(mongoUser1));

        // WHEN
        MongoUserDTOResponse expected = mongoUserDTOResponse1;
        MongoUserDTOResponse actual = mongoUserService.getMe(principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void getMe_whenPrincipalUsernameNotInRepo_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUser1.username());
        when(mongoUserRepository.findMongoUserByUsername(mongoUser1.username())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(MongoUserExceptionNotFound.class, () -> mongoUserService.getMe(principal));
    }

    @Test
    void getMongoUserDTOResponseById_whenIdInRepo_thenReturnDTOResponse() {
        // GIVEN
        when(mongoUserRepository.findById(mongoUser1.id())).thenReturn(Optional.of(mongoUser1));

        // WHEN
        MongoUserDTOResponse expected = mongoUserDTOResponse1;
        MongoUserDTOResponse actual = mongoUserService.getMongoUserDTOResponseById(mongoUser1.id());

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void getMongoUserDTOResponseById_whenIdNotInRepo_thenThrowException() {
        // GIVEN
        when(mongoUserRepository.findById(mongoUser1.id())).thenReturn(Optional.empty());
        String id = mongoUser1.id();

        // WHEN & THEN
        assertThrows(MongoUserExceptionNotFound.class, () -> mongoUserService.getMongoUserDTOResponseById(id));
    }

    @Test
    void getMongoUserDTOResponseByUsername_whenUsernameInRepo_thenReturnDTOResponse() {
        // GIVEN
        when(mongoUserRepository.findMongoUserByUsername(mongoUser1.username())).thenReturn(Optional.of(mongoUser1));

        // WHEN
        MongoUserDTOResponse expected = mongoUserDTOResponse1;
        MongoUserDTOResponse actual = mongoUserService.getMongoUserDTOResponseByUsername(mongoUser1.username());

        // THEN
        assertEquals(expected, actual);
    }
}
