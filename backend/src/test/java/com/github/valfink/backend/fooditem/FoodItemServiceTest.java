package com.github.valfink.backend.fooditem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FoodItemServiceTest {
    FoodItemRepository foodItemRepository;
    FoodItemService foodItemService;
    FoodItem foodItem1;

    @BeforeEach
    void setUp() {
        foodItemRepository = mock(FoodItemRepository.class);
        foodItemService = new FoodItemService(foodItemRepository);
        foodItem1 = new FoodItem(
                "1",
                "Food Item 1",
                "https://photo.com/1.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
    }

    @Test
    void getAll_whenRepoReturnsListOfOneItem_thenReturnListOfOneItem() {
        // WHEN
        List<FoodItem> expected = new ArrayList<>(List.of(foodItem1));
        when(foodItemRepository.findAll()).thenReturn(expected);
        List<FoodItem> actual = foodItemService.getAllFoodItems();

        // THEN
        assertEquals(expected, actual);
    }
}
