package com.github.valfink.backend.fooditem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FoodItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FoodItemRepository foodItemRepository;
    FoodItem foodItem1;
    List<FoodItem> foodItemListOfOne;

    @BeforeEach
    void setUp() {
        foodItem1 = new FoodItem(
                "1",
                "Food Item 1",
                "https://photo.com/1.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        foodItemListOfOne = new ArrayList<>(List.of(foodItem1));
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
        foodItemRepository.save(foodItem1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/food"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": "1",
                                "title": "Food Item 1",
                                "photo_uri": "https://photo.com/1.jpg",
                                "location": "Berlin",
                                "pickup_until": "2023-03-16T11:14:00Z",
                                "consume_until": "2023-03-18T11:00:00Z",
                                "description": "This is my first food item."
                            }
                        ]
                        """));
    }
}