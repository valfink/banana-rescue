package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
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
    MongoUserService mongoUserService;
    FoodItemService foodItemService;
    FoodItem foodItem1;
    FoodItemDTOResponse foodItemDTOResponse1;

    @BeforeEach
    void setUp() {
        foodItemRepository = mock(FoodItemRepository.class);
        mongoUserService = mock(MongoUserService.class);
        foodItemService = new FoodItemService(foodItemRepository, mongoUserService);
        foodItem1 = new FoodItem(
                "1",
                "1",
                "Food Item 1",
                "https://photo.com/1.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        foodItemDTOResponse1 = new FoodItemDTOResponse(
                foodItem1.id(),
                new MongoUserDTOResponse(foodItem1.donator_id(), "user"),
                foodItem1.title(),
                foodItem1.photo_uri(),
                foodItem1.location(),
                foodItem1.pickup_until(),
                foodItem1.consume_until(),
                foodItem1.description()
        );
    }

    @Test
    void getAll_whenRepoReturnsListOfOneItem_thenReturnListOfOneItem() {
        // GIVEN
        when(foodItemRepository.findAll()).thenReturn(new ArrayList<>(List.of(foodItem1)));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donator_id())).thenReturn(new MongoUserDTOResponse(foodItem1.donator_id(), "user"));

        // WHEN
        List<FoodItemDTOResponse> expected = new ArrayList<>(List.of(foodItemDTOResponse1));
        List<FoodItemDTOResponse> actual = foodItemService.getAllFoodItems();

        // THEN
        assertEquals(expected, actual);
    }
}
