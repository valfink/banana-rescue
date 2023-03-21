package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import com.github.valfink.backend.util.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.InputMismatchException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;
    private final MongoUserService mongoUserService;
    private final IdService idService;
    private final PhotoService photoService;

    public List<FoodItemDTOResponse> getAllFoodItems() {
        return foodItemRepository.getAllByOrderByPickupUntilDesc()
                .stream()
                .map(item -> new FoodItemDTOResponse(
                        item.id(),
                        mongoUserService.getMongoUserDTOResponseById(item.donatorId()),
                        item.title(),
                        item.photoUri(),
                        item.location(),
                        item.pickupUntil(),
                        item.consumeUntil(),
                        item.description()
                ))
                .toList();
    }

    public FoodItemDTOResponse addFoodItem(FoodItemDTORequest foodItemDTORequest, MultipartFile photo, Principal principal) {
        if (foodItemDTORequest.title() == null || foodItemDTORequest.title().isBlank()) {
            throw new InputMismatchException("Title must not be blank");
        }
        if (foodItemDTORequest.location() == null || foodItemDTORequest.location().isBlank()) {
            throw new InputMismatchException("Location must not be blank");
        }
        if (foodItemDTORequest.pickupUntil() == null) {
            throw new InputMismatchException("Pickup until must not be blank");
        }
        if (foodItemDTORequest.consumeUntil() == null) {
            throw new InputMismatchException("Consume until must not be blank");
        }
        if (foodItemDTORequest.description() == null || foodItemDTORequest.description().isBlank()) {
            throw new InputMismatchException("Description must not be blank");
        }
        String photoUri;
        try {
            photoUri = photoService.uploadPhoto(photo);
        } catch (IOException e) {
            throw new InputMismatchException("The photo upload didn't work: " + e.getMessage());
        }
        FoodItem foodItem = foodItemRepository.save(new FoodItem(
                idService.generateId(),
                mongoUserService.getMongoUserDTOResponseByUsername(principal.getName()).id(),
                foodItemDTORequest.title(),
                photoUri,
                foodItemDTORequest.location(),
                foodItemDTORequest.pickupUntil(),
                foodItemDTORequest.consumeUntil(),
                foodItemDTORequest.description()
        ));
        return new FoodItemDTOResponse(
                foodItem.id(),
                mongoUserService.getMongoUserDTOResponseById(foodItem.donatorId()),
                foodItem.title(),
                foodItem.photoUri(),
                foodItem.location(),
                foodItem.pickupUntil(),
                foodItem.consumeUntil(),
                foodItem.description()
        );
    }
}
