package com.github.valfink.backend.mongouser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MongoUserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    MongoUser mongoUser1;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("1", "user", passwordEncoder.encode("password"), "BASIC");
    }

    @Test
    @DirtiesContext
    void signUp_whenValidInput_thenReturnNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "username": "user",
                                "password": "pass"
                                }
                                """)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "username": "user"
                        }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DirtiesContext
    void signUp_whenInvalidInput_thenReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "username": "user"
                                }
                                """)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMe_whenPrincipalUsernameIsInRepo_thenReturnUser() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "id": "1",
                        "username": "user"
                        }
                        """));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMe_whenPrincipalUsernameIsNotInRepo_thenReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void getMe_whenNotAuthenticated_thenReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void login_whenUserExists_thenReturnUserInfos() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .with(httpBasic("user", "password"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "id": "1",
                        "username": "user"
                        }
                        """));
    }

    @Test
    @DirtiesContext
    void login_whenUsernameIsNotInRepo_thenReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .with(httpBasic("user", "password"))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void logout_whenLoggId_thenStatusOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}