package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FoodItemServiceTest {
    FoodItemRepository foodItemRepository;
    MongoUserService mongoUserService;
    IdService idService;
    FoodItemService foodItemService;
    Principal principal;
    FoodItem foodItem1;
    FoodItemDTOResponse foodItemDTOResponse1;
    MongoUserDTOResponse mongoUserDTOResponse1;

    @BeforeEach
    void setUp() {
        foodItemRepository = mock(FoodItemRepository.class);
        mongoUserService = mock(MongoUserService.class);
        idService = mock(IdService.class);
        principal = mock(Principal.class);
        foodItemService = new FoodItemService(foodItemRepository, mongoUserService, idService);
        mongoUserDTOResponse1 = new MongoUserDTOResponse("1", "user");
        foodItem1 = new FoodItem(
                "1",
                mongoUserDTOResponse1.id(),
                "Food Item 1",
                "PHOTO URI",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        foodItemDTOResponse1 = new FoodItemDTOResponse(
                foodItem1.id(),
                mongoUserDTOResponse1,
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

    @Test
    void addFoodItem_whenValidDTORequest_thenReturnDTOResponse() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickup_until(), foodItem1.consume_until(), foodItem1.description());
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(idService.generateId()).thenReturn("1");
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.save(foodItem1)).thenReturn(foodItem1);
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse1.id())).thenReturn(mongoUserDTOResponse1);

        // WHEN
        FoodItemDTOResponse expected = foodItemDTOResponse1;
        FoodItemDTOResponse actual = foodItemService.addFoodItem(foodItemDTORequest, principal);

        // THEN
        verify(foodItemRepository).save(foodItem1);
        assertEquals(expected, actual);
    }

    @Test
    void addFoodItem_whenNoTitle_thenThrowException() {
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(new FoodItemDTORequest(null, foodItem1.location(), foodItem1.pickup_until(), foodItem1.consume_until(), foodItem1.description()), principal));
    }

    @Test
    void addFoodItem_whenNoLocation_thenThrowException() {
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(new FoodItemDTORequest(foodItem1.title(), "", foodItem1.pickup_until(), foodItem1.consume_until(), foodItem1.description()), principal));
    }

    @Test
    void addFoodItem_whenNoPickupUntil_thenThrowException() {
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), null, foodItem1.consume_until(), foodItem1.description()), principal));
    }

    @Test
    void addFoodItem_whenNoConsumeUntil_thenThrowException() {
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickup_until(), null, foodItem1.description()), principal));
    }

    @Test
    void addFoodItem_whenNoDescription_thenThrowException() {
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickup_until(), foodItem1.consume_until(), "   "), principal));
    }
}
