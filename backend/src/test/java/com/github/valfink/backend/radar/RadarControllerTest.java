package com.github.valfink.backend.radar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RadarControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    RadarRepository radarRepository;
    @Autowired
    ObjectMapper objectMapper;
    MongoUser mongoUser1;
    Radar radar1, radar2;
    RadarDTO radarDTO1;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("1", "user", "pass", "BASIC");
        radar1 = new Radar(mongoUser1.id(), new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599")), 200);
        radar2 = new Radar(mongoUser1.id(), new Coordinate(new BigDecimal("20"), new BigDecimal("40")), 20);
        radarDTO1 = new RadarDTO(radar1.center(), radar1.radiusInMeters());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addRadar_whenUserHasNoRadarYetAndDTOIsValid_thenReturnNewRadarDTO() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/my-radar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(radarDTO1))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(radarDTO1)));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addRadar_whenUserAlreadyHasRadar_thenReturn400() throws Exception {
        mongoUserRepository.save(mongoUser1);
        radarRepository.save(radar2);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/my-radar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(radarDTO1))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}