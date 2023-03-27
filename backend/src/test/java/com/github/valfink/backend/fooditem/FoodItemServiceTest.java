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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        assertThrows(FoodItemExceptionPhotoAction.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoTitle_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(null, foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(FoodItemExceptionBadInputData.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoLocation_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), "", foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(FoodItemExceptionBadInputData.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoPickupUntil_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), null, foodItem1.consumeUntil(), foodItem1.description());
        // WHEN & THEN
        assertThrows(FoodItemExceptionBadInputData.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoConsumeUntil_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), null, foodItem1.description());
        // WHEN & THEN
        assertThrows(FoodItemExceptionBadInputData.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
    }

    @Test
    void addFoodItem_whenNoDescription_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest(foodItem1.title(), foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), "     ");
        // WHEN & THEN
        assertThrows(FoodItemExceptionBadInputData.class, () -> foodItemService.addFoodItem(foodItemDTORequest, multipartFile, principal));
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
        when(foodItemRepository.findById("1")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(FoodItemExceptionNotFound.class, () -> foodItemService.getFoodItemById("1"));
    }

    @Test
    void updateFoodItemById_whenIdIsInRepoAndRequestIsValid_thenReturnUpdatedItem() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest("New title", "New location", foodItem1.pickupUntil(), foodItem1.consumeUntil(), "New description");
        FoodItem updatedFoodItem = new FoodItem(foodItem1.id(), foodItem1.donatorId(), foodItemDTORequest.title(), foodItem1.photoUri(), foodItemDTORequest.location(), foodItemDTORequest.pickupUntil(), foodItemDTORequest.consumeUntil(), foodItemDTORequest.description());
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.save(updatedFoodItem)).thenReturn(updatedFoodItem);

        // WHEN
        FoodItemDTOResponse expected = new FoodItemDTOResponse(foodItemDTOResponse1.id(), foodItemDTOResponse1.donator(), foodItemDTORequest.title(), foodItemDTOResponse1.photoUri(), foodItemDTORequest.location(), foodItemDTORequest.pickupUntil(), foodItemDTORequest.consumeUntil(), foodItemDTORequest.description());
        FoodItemDTOResponse actual = foodItemService.updateFoodItemById(foodItem1.id(), foodItemDTORequest, null, principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void updateFoodItemById_whenUserIsNotDonator_thenThrowException() {
        // GIVEN
        FoodItemDTORequest foodItemDTORequest = new FoodItemDTORequest("New title", "New location", foodItem1.pickupUntil(), foodItem1.consumeUntil(), "New description");
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(new MongoUserDTOResponse("2", "other user"));
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        String foodItemId = foodItem1.id();

        // WHEN & THEN
        assertThrows(FoodItemExceptionAuthorization.class, () -> foodItemService.updateFoodItemById(foodItemId, foodItemDTORequest, null, principal));
    }

    @Test
    void deletePhotoFromFoodItem_whenEverythingIsValid_thenReturnCloudinaryResponse() throws IOException {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        when(photoService.deletePhoto(foodItem1.photoUri())).thenReturn("ok");

        // WHEN
        String expected = "ok";
        String actual = foodItemService.deletePhotoFromFoodItem(foodItem1.id(), principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void deletePhotoFromFoodItem_whenUserIsNotDonator_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(new MongoUserDTOResponse("2", "other user"));
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        String id = foodItem1.id();

        // WHEN & THEN
        assertThrows(FoodItemExceptionAuthorization.class, () -> foodItemService.deletePhotoFromFoodItem(id, principal));
    }

    @Test
    void deletePhotoFromFoodItem_whenItemHasNoImage_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(new FoodItem(foodItem1.id(), foodItem1.donatorId(), foodItem1.title(), null, foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description())));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        String id = foodItem1.id();

        // WHEN & THEN
        assertThrows(FoodItemExceptionDataMismatch.class, () -> foodItemService.deletePhotoFromFoodItem(id, principal));
    }

    @Test
    void deletePhotoFromFoodItem_whenCloudinaryThrowsException_thenThrowException() throws IOException {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        when(photoService.deletePhoto(foodItem1.photoUri())).thenThrow(IOException.class);
        String id = foodItem1.id();

        // WHEN & THEN
        assertThrows(FoodItemExceptionPhotoAction.class, () -> foodItemService.deletePhotoFromFoodItem(id, principal));
    }

    @Test
    void deleteFoodItemById_whenItemExistsAndBelongsToPrincipal_thenReturnDeletedItem() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);

        // WHEN
        FoodItemDTOResponse expected = foodItemDTOResponse1;
        FoodItemDTOResponse actual = foodItemService.deleteFoodItemById(foodItem1.id(), principal);

        // THEN
        assertEquals(expected, actual);
        verify(foodItemRepository).deleteById(foodItem1.id());
    }

    @Test
    void deleteFoodItemById_whenUserIsNotDonator_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(new MongoUserDTOResponse("2", "other user"));
        when(foodItemRepository.findById(foodItem1.id())).thenReturn(Optional.of(foodItem1));
        when(mongoUserService.getMongoUserDTOResponseById(foodItem1.donatorId())).thenReturn(mongoUserDTOResponse1);
        String id = foodItem1.id();

        // WHEN & THEN
        assertThrows(FoodItemExceptionAuthorization.class, () -> foodItemService.deleteFoodItemById(id, principal));
    }
}
