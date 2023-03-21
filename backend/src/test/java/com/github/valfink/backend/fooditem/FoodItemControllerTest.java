package com.github.valfink.backend.fooditem;

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

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FoodItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FoodItemRepository foodItemRepository;
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
                "https://photo.com/1.jpg",
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
                                "photoUri": "https://photo.com/1.jpg",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                            }
                        ]
                        """));
    }

    @Test
    @WithMockUser
    void addFoodItem_whenPostingValidItemAndSignedIn_thenReturnNewItem() throws Exception {
        mongoUserRepository.save(mongoUser1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/food")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                                }
                                """)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                {
                                "title": "Food Item 1",
                                "location": "Berlin",
                                "pickupUntil": "2023-03-16T11:14:00Z",
                                "consumeUntil": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                                }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }
}