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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;
    private final MongoUserService mongoUserService;
    private final IdService idService;
    private final PhotoService photoService;

    private FoodItemDTOResponse foodItemDTOResponseFromFoodItem(FoodItem foodItem) {
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

    private void throwExceptionIfFoodItemDTORequestIsNotValid(FoodItemDTORequest foodItemDTORequest) {
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
    }

    private String uploadPhotoIfPresentAndReturnUriOrNull(MultipartFile photo) {
        String photoUri;
        if (photo != null) {
            try {
                photoUri = photoService.uploadPhoto(photo);
            } catch (IOException e) {
                throw new InputMismatchException("The photo upload didn't work: " + e.getMessage());
            }
        } else {
            photoUri = null;
        }

        return photoUri;
    }

    public List<FoodItemDTOResponse> getAllFoodItems() {
        return foodItemRepository.getAllByOrderByPickupUntilDesc()
                .stream()
                .map(this::foodItemDTOResponseFromFoodItem)
                .toList();
    }

    public FoodItemDTOResponse addFoodItem(FoodItemDTORequest foodItemDTORequest, MultipartFile photo, Principal principal) {

        throwExceptionIfFoodItemDTORequestIsNotValid(foodItemDTORequest);

        String photoUri = uploadPhotoIfPresentAndReturnUriOrNull(photo);

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

        return foodItemDTOResponseFromFoodItem(foodItem);
    }

    public FoodItemDTOResponse getFoodItemById(String id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("The task with the id " + id + " doesn't exist."));

        return foodItemDTOResponseFromFoodItem(foodItem);
    }

    public FoodItemDTOResponse updateFoodItemById(String id, FoodItemDTORequest foodItemDTORequest, MultipartFile photo, Principal principal) {
        String userId = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName()).id();
        FoodItemDTOResponse oldFoodItem = getFoodItemById(id);

        if (!oldFoodItem.donator().id().equals(userId)) {
            throw new SecurityException("You may only edit you own items!");
        }
        throwExceptionIfFoodItemDTORequestIsNotValid(foodItemDTORequest);

        String photoUri;
        if (oldFoodItem.photoUri() == null || oldFoodItem.photoUri().isBlank()) {
            photoUri = uploadPhotoIfPresentAndReturnUriOrNull(photo);
        } else {
            photoUri = oldFoodItem.photoUri();
        }

        FoodItem foodItem = foodItemRepository.save(new FoodItem(
                id,
                userId,
                foodItemDTORequest.title(),
                photoUri,
                foodItemDTORequest.location(),
                foodItemDTORequest.pickupUntil(),
                foodItemDTORequest.consumeUntil(),
                foodItemDTORequest.description()
        ));

        return foodItemDTOResponseFromFoodItem(foodItem);
    }
}
