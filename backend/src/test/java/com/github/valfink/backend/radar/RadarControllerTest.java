package com.github.valfink.backend.radar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valfink.backend.fooditem.FoodItem;
import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemRepository;
import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.geolocation.Location;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
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
import java.time.Instant;
import java.util.List;

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
    FoodItemRepository foodItemRepository;
    @Autowired
    ObjectMapper objectMapper;
    MongoUser mongoUser1, mongoUser2;
    Radar radar1, radar2;
    RadarDTORequest radarDTORequest1;
    FoodItem foodItem1CloseBy, foodItem2NotSoClose;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("u1", "user", "pass", "BASIC");
        mongoUser2 = new MongoUser("u2", "user2", "pass", "BASIC");
        radar1 = new Radar(mongoUser1.id(), new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599")), 200);
        radar2 = new Radar(mongoUser1.id(), new Coordinate(new BigDecimal("20"), new BigDecimal("40")), 20);
        radarDTORequest1 = new RadarDTORequest(radar1.center(), radar1.radiusInMeters());
        foodItem1CloseBy = new FoodItem(
                "f1",
                mongoUser2.id(),
                "Food Item Close By",
                null,
                new Location("Very close", new Coordinate(new BigDecimal("52.51837"), new BigDecimal("13.38855"))),
                Instant.parse("2023-04-12T11:30:00Z"),
                Instant.parse("2023-04-14T12:00:00Z"),
                "This is my food item close by."
        );
        foodItem2NotSoClose = new FoodItem(
                "f2",
                mongoUser2.id(),
                "Food Item Not So Close",
                null,
                new Location("Not So close", new Coordinate(new BigDecimal("52.51971"), new BigDecimal("13.38824"))),
                Instant.parse("2023-04-12T11:30:00Z"),
                Instant.parse("2023-04-14T12:00:00Z"),
                "This is my food item not so close, but not very far away."
        );
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addMyRadar_whenUserHasNoRadarYetAndDTOIsValid_thenReturnNewRadarDTO() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/my-radar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(radarDTORequest1))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(radarDTORequest1)));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addMyRadar_whenUserAlreadyHasRadar_thenReturn400() throws Exception {
        mongoUserRepository.save(mongoUser1);
        radarRepository.save(radar2);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/my-radar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(radarDTORequest1))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMyRadar_whenUserHasNoRadar_thenReturn404() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/my-radar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMyRadar_whenUserHasRadar_thenReturnRadar() throws Exception {
        mongoUserRepository.save(mongoUser1);
        radarRepository.save(radar1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/my-radar"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(radar1.convertToDTOResponse(List.of()))));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMyRadar_whenUserHasRadarAndFoodItemIsCloseBy_thenReturnRadarDTOWithItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(mongoUser2);
        foodItemRepository.save(foodItem1CloseBy);
        foodItemRepository.save(foodItem2NotSoClose);
        radarRepository.save(radar1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/my-radar"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(radar1.convertToDTOResponse(List.of(
                        new FoodItemDTOResponse(foodItem1CloseBy.id(),
                                new MongoUserDTOResponse(mongoUser2.id(),
                                        mongoUser2.username()),
                                foodItem1CloseBy.title(),
                                foodItem1CloseBy.photoUri(),
                                foodItem1CloseBy.location(),
                                foodItem1CloseBy.pickupUntil(),
                                foodItem1CloseBy.consumeUntil(),
                                foodItem1CloseBy.description())
                )))));
    }
}