package com.github.valfink.backend.fooditem;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FoodItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FoodItemRepository foodItemRepository;
    @MockBean
    Cloudinary cloudinary;
    Uploader uploader = mock(Uploader.class);
    @Autowired
    MongoUserRepository mongoUserRepository;
    MongoUser mongoUser1;
    FoodItem foodItem1;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("1", "user", "pass", "BASIC");
        foodItem1 = new FoodItem(
                "1",
                mongoUser1.id(),
                "Food Item 1",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
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
                .andExpect(content().json("""
                        [
                            {
                                "id": "1",
                                "title": "Food Item 1",
                                "photoUri": "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item.",
                                "donator": {
                                    "id": "1",
                                    "username": "user"
                                }
                            }
                        ]
                        """));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingValidItemWithoutPhotoAndSignedIn_thenReturnNewItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", """
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                                }
                                """.getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item.",
                                "donator": {
                                    "id": "1",
                                    "username": "user"
                                }
                                }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void addFoodItem_whenPostingValidItemIncludingPhotoAndSignedIn_thenReturnNewItem() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(Map.of("url", "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg"));
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/food")
                        .file(new MockMultipartFile("form", null,
                                "application/json", """
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                                }
                                """.getBytes()))
                        .file(new MockMultipartFile("photo", "content".getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item.",
                                "photoUri": "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                                "donator": {
                                    "id": "1",
                                    "username": "user"
                                }
                                }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DirtiesContext
    void getFoodItemById_whenIdIsInRepo_thenReturnValidResponse() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": "1",
                            "title": "Food Item 1",
                            "photoUri": "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                            "location": "Berlin",
                            "pickupUntil": "2023-03-16T11:14:00Z",
                            "consumeUntil": "2023-03-18T11:00:00Z",
                            "description": "This is my first food item.",
                            "donator": {
                                "id": "1",
                                "username": "user"
                            }
                        }
                        """));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void updateFoodItemById_whenIdIsInRepoAndUserIsDonator_thenReturnUpdatedItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/food/1")
                        .file(new MockMultipartFile("form", null,
                                "application/json", """
                                {
                                "title": "Updated Title",
                                "location": "New Location",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "Description is updated."
                                }
                                """.getBytes()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "id": "1",
                        "title": "Updated Title",
                        "location": "New Location",
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
}
