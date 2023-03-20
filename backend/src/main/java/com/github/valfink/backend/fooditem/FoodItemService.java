package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.mongouser.MongoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;
    private final MongoUserService mongoUserService;

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
}
