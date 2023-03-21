package com.github.valfink.backend.fooditem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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

    @PostMapping
    public FoodItemDTOResponse addFoodItem(@RequestPart("form") FoodItemDTORequest foodItemDTORequest, @RequestPart(value = "photo", required = false) MultipartFile photo, Principal principal) {
        return foodItemService.addFoodItem(foodItemDTORequest, photo, principal);
    }
}
