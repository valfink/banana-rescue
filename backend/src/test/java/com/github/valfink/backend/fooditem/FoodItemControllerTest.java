package com.github.valfink.backend.fooditem;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.geolocation.Location;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserRepository;
import com.github.valfink.backend.radar.Radar;
import com.github.valfink.backend.radar.RadarRepository;
import com.github.valfink.backend.util.IdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FoodItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FoodItemRepository foodItemRepository;
    @MockBean
    Cloudinary cloudinary;
    @MockBean
    IdService idService;
    Uploader uploader = mock(Uploader.class);
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    RadarRepository radarRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @LocalServerPort
    private int port;
    MongoUser mongoUser1, mongoUser2;
    String rawPasswordForAllTestUsers;
    FoodItem foodItem1, foodItem2;
    FoodItemDTORequest foodItemDTORequest1, updatedFoodItemDTORequest1;
    FoodItemDTOResponse foodItemDTOResponse1;

    @BeforeEach
    void setUp() {
        rawPasswordForAllTestUsers = "password";
        mongoUser1 = new MongoUser("1", "user", passwordEncoder.encode(rawPasswordForAllTestUsers), "BASIC");
        mongoUser2 = new MongoUser("2", "user2", passwordEncoder.encode(rawPasswordForAllTestUsers), "BASIC");
        foodItem1 = new FoodItem(
                "1",
                mongoUser1.id(),
                "Food Item 1",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                new Location("Berlin", new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599"))),
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        foodItem2 = new FoodItem(
                "2",
                mongoUser1.id(),
                "Food Item 2",
                null,
                new Location("Berlin", new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599"))),
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my second food item."
        );
        foodItemDTORequest1 = new FoodItemDTORequest(
                foodItem1.title(),
                foodItem1.location(),
                foodItem1.pickupUntil(),
                foodItem1.consumeUntil(),
                foodItem1.description()
        );
        updatedFoodItemDTORequest1 = new FoodItemDTORequest(
                "Updated Title",
                new Location("New Location", new Coordinate(new BigDecimal("0"), new BigDecimal("0"))),
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "Description is updated."
        );
        foodItemDTOResponse1 = new FoodItemDTOResponse(
                foodItem1.id(),
                new MongoUserDTOResponse(mongoUser1.id(), mongoUser1.username()),
                foodItem1.title(),
                foodItem1.photoUri(),
                foodItem1.location(),
                foodItem1.pickupUntil(),
                foodItem1.consumeUntil(),
                foodItem1.description()
        );
    }

    @Test
    @DirtiesContext
    void getAll_whenNoItemInRepo_thenReturnEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DirtiesContext
    void getAll_whenOneItemInRepo_thenReturnListOfOneItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(foodItemDTOResponse1))));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingValidItemWithoutPhotoAndSignedIn_thenReturnNewItem() throws Exception {
        when(idService.generateId()).thenReturn(foodItem1.id());
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", objectMapper.writeValueAsString(foodItemDTORequest1).getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new FoodItemDTOResponse(foodItem1.id(), foodItemDTOResponse1.donator(), foodItem1.title(), null, foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description()))));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingInvalidItem_thenReturn400() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", """
                                {
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                                }
                                """.getBytes()))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingValidItemIncludingPhotoAndSignedIn_thenReturnNewItem() throws Exception {
        when(idService.generateId()).thenReturn(foodItem1.id());
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", foodItem1.photoUri()));
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", objectMapper.writeValueAsString(foodItemDTORequest1).getBytes()))
                        .file(new MockMultipartFile("photo", "content".getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(foodItemDTOResponse1)));
    }

    @Test
    @DirtiesContext
    void getFoodItemById_whenIdIsInRepo_thenReturnValidResponse() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(foodItemDTOResponse1)));
    }

    @Test
    @DirtiesContext
    void getFoodItemById_whenIdIsNotInRepo_thenReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void updateFoodItemById_whenIdIsInRepoAndUserIsDonator_thenReturnUpdatedItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/food/1")
                        .file(new MockMultipartFile("form", null,
                                "application/json", objectMapper.writeValueAsString(updatedFoodItemDTORequest1).getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "id": "1",
                        "title": "Updated Title",
                        "location": {
                            "title":"New Location",
                            "coordinate": {
                                "latitude": 0,
                                "longitude": 0
                                }
                            },
                        "pickupUntil": "2023-03-16T11:14:00Z",
                        "consumeUntil": "2023-03-18T11:00:00Z",
                        "description": "Description is updated.",
                        "donator": {
                            "id": "1",
                            "username": "user"
                        }
                        }
                        """));
    }

    @Test
    @DirtiesContext
    @WithMockUser("user2")
    void updateFoodItemById_whenUserIsNotDonator_thenReturn403() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(mongoUser2);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/food/1")
                        .file(new MockMultipartFile("form", null,
                                "application/json", objectMapper.writeValueAsString(updatedFoodItemDTORequest1).getBytes()))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void deletePhotoFromFoodItem_whenEverythingIsValid_thenReturnOk() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/1/photo")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void deletePhotoFromFoodItem_whenCloudinaryThrowsException_thenReturn500() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(), anyMap())).thenThrow(IOException.class);
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/1/photo")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void deletePhotoFromFoodItem_whenFoodItemHasNoImage_thenReturn400() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem2);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/2/photo")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void deleteFoodItemById_whenEverythingIsValid_thenReturnOk() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(foodItemDTOResponse1)));
    }

    @Test
    @DirtiesContext
    @WithMockUser("user2")
    void deleteFoodItemById_whenUserIsNotDonator_thenReturn403() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(new MongoUser("2", "user2", "pass", "BASIC"));
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/food/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMyFoodItems_whenUserHasOneItem_thenReturnListOfOneItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/my-items"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(foodItemDTOResponse1))));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingValidItemWithinSomeonesRadar_thenNotifyOtherUser() throws Exception {
        // GIVEN
        when(idService.generateId()).thenReturn(foodItem1.id());
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", foodItem1.photoUri()));
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(mongoUser2);
        radarRepository.save(new Radar(mongoUser2.id(), new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599")), 5000));
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(mappingJackson2MessageConverter);
        CompletableFuture<FoodItemDTOResponse> completableFuture = new CompletableFuture<>();
        StompFrameHandler stompFrameHandler = new FoodItemStompFrameHandler(completableFuture);
        HttpHeaders httpHeaders = new HttpHeaders();
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders(httpHeaders);
        String authorization = mongoUser2.username() + ":" + rawPasswordForAllTestUsers;
        httpHeaders.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(authorization.getBytes())));
        StompSession stompSession = stompClient
                .connectAsync("ws://localhost:" + port + "/api/ws", wsHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);
        stompSession.subscribe("/user/queue/radar", stompFrameHandler);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", objectMapper.writeValueAsString(foodItemDTORequest1).getBytes()))
                        .file(new MockMultipartFile("photo", "content".getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(foodItemDTOResponse1)));

        FoodItemDTOResponse actual = completableFuture.get(1, TimeUnit.SECONDS);
        assertEquals(foodItemDTOResponse1, actual);
    }

    private record FoodItemStompFrameHandler(
            CompletableFuture<FoodItemDTOResponse> completableFuture) implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return FoodItemDTOResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            if (payload instanceof FoodItemDTOResponse) {
                completableFuture.complete((FoodItemDTOResponse) payload);
            }
        }
    }
}
