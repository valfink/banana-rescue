package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.InputMismatchException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;
    private final MongoUserService mongoUserService;
    private final IdService idService;

    public List<FoodItemDTOResponse> getAllFoodItems() {
        return foodItemRepository.findAll()
                .stream()
                .map(item -> new FoodItemDTOResponse(
                        item.id(),
                        mongoUserService.getMongoUserDTOResponseById(item.donator_id()),
                        item.title(),
                        item.photo_uri(),
                        item.location(),
                        item.pickup_until(),
                        item.consume_until(),
                        item.description()
                ))
                .toList();
    }

    public FoodItemDTOResponse addFoodItem(FoodItemDTORequest foodItemDTORequest, Principal principal) {
        if (foodItemDTORequest.title() == null || foodItemDTORequest.title().isBlank()) {
            throw new InputMismatchException("Title must not be blank");
        }
        if (foodItemDTORequest.location() == null || foodItemDTORequest.location().isBlank()) {
            throw new InputMismatchException("Location must not be blank");
        }
        if (foodItemDTORequest.pickup_until() == null) {
            throw new InputMismatchException("Pickup until must not be blank");
        }
        if (foodItemDTORequest.consume_until() == null) {
            throw new InputMismatchException("Consume until must not be blank");
        }
        if (foodItemDTORequest.description() == null || foodItemDTORequest.description().isBlank()) {
            throw new InputMismatchException("Description must not be blank");
        }
        FoodItem foodItem = foodItemRepository.save(new FoodItem(
                idService.generateId(),
                mongoUserService.getMongoUserDTOResponseByUsername(principal.getName()).id(),
                foodItemDTORequest.title(),
                // TODO: PHOTO UPLOAD!
                "PHOTO URI",
                foodItemDTORequest.location(),
                foodItemDTORequest.pickup_until(),
                foodItemDTORequest.consume_until(),
                foodItemDTORequest.description()
        ));
        return new FoodItemDTOResponse(
                foodItem.id(),
                mongoUserService.getMongoUserDTOResponseById(foodItem.donator_id()),
                foodItem.title(),
                foodItem.photo_uri(),
                foodItem.location(),
                foodItem.pickup_until(),
                foodItem.consume_until(),
                foodItem.description()
        );
    }
}
