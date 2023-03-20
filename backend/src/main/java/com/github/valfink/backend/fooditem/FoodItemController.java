package com.github.valfink.backend.fooditem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/food")
public class FoodItemController {
    private final FoodItemService foodItemService;

    @GetMapping
    public List<FoodItemDTOResponse> getAllFoodItems() {
        return foodItemService.getAllFoodItems();
    }
}
