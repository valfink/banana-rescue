package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import com.github.valfink.backend.util.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FoodItemServiceTest {
    FoodItemRepository foodItemRepository;
    MongoUserService mongoUserService;
    IdService idService;
    PhotoService photoService;
    FoodItemService foodItemService;
    Principal principal;
    MultipartFile multipartFile;
    FoodItem foodItem1;
    FoodItemDTOResponse foodItemDTOResponse1;
    MongoUserDTOResponse mongoUserDTOResponse1;

    @BeforeEach
    void setUp() {
        foodItemRepository = mock(FoodItemRepository.class);
        mongoUserService = mock(MongoUserService.class);
        idService = mock(IdService.class);
        photoService = mock(PhotoService.class);
        principal = mock(Principal.class);
        multipartFile = mock(MultipartFile.class);
        foodItemService = new FoodItemService(foodItemRepository, mongoUserService, idService, photoService);
        mongoUserDTOResponse1 = new MongoUserDTOResponse("1", "user");
        foodItem1 = new FoodItem(
                "1",
                mongoUserDTOResponse1.id(),
                "Food Item 1",
                "https://photo.com/1.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        foodItemDTOResponse1 = new FoodItemDTOResponse(
                foodItem1.id(),
                mongoUserDTOResponse1,
                foodItem1.title(),
                foodItem1.photoUri(),
                foodItem1.location(),
                foodItem1.pickupUntil(),
                foodItem1.consumeUntil(),
                foodItem1.description()
        );
    }

    @Test
    void getAll_whenRepoReturnsListOfOneItem_thenReturnListOfOneItem() {
        // GIVEN
        when(foodItemRepository.getAllByOrderByPickupUntilDesc()).thenReturn(new ArrayList<>(List.of(foodItem1)));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(new MongoUserDTOResponse(foodItem1.donatorId(), "user"));

        // WHEN
        List<FoodItemDTOResponse> expected = new ArrayList<>(List.of(foodItemDTOResponse1));
        List<FoodItemDTOResponse> actual = foodItemService.getAllFoodItems();

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void addFoodItem_whenValidDTORequest_thenReturnDTOResponse() throws IOException {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        when(photoService.uploadPhoto(multipartFile)).thenReturn(foodItem1.photoUri());
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(idService.generateId()).thenReturn("1");
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.save(foodItem1)).thenReturn(foodItem1);
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse1.id())).thenReturn(mongoUserDTOResponse1);

        // WHEN
        FoodItemDTOResponse expected = foodItemDTOResponse1;
        FoodItemDTOResponse actual = foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal);

        // THEN
        verify(foodItemRepository).save(foodItem1);
        assertEquals(expected, actual);
    }

    @Test
    void addFoodItem_whenPhotoUploadFails_thenThrowException() throws IOException {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        when(photoService.uploadPhoto(multipartFile)).thenThrow(IOException.class);

        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoTitle_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(null, foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoLocation_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), "", foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoPickupUntil_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), null, foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoConsumeUntil_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), null, foodItem1.description());
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoDescription_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), "     ");
        // WHEN & THEN
        assertThrows(InputMismatchException.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void getFoodItemById_whenIdIsInRepo_thenReturnDTO() {
        // GIVEN
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse1.id())).thenReturn(mongoUserDTOResponse1);

        // WHEN
        FoodItemDTOResponse expected = foodItemDTOResponse1;
        FoodItemDTOResponse actual = foodItemService.getFoodItemById(foodItem1.id());

        // THEN
        assertEquals(expected, actual);
        verify(foodItemRepository).findById(foodItem1.id());
        verify(mongoUserService).getMongoUserDTOResponseById(mongoUserDTOResponse1.id());
    }

    @Test
    void getFoodItemById_whenIdIsNotInRepo_thenThrowException() {
        // GIVEN
        when(foodItemRepository.findById("1π")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> foodItemService.getFoodItemById("1"));
    }
}
