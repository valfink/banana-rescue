package com.github.valfink.backend.fooditem;

import com.github.valfink.backend.radar.RadarService;
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
    private final RadarService radarService;

    @GetMapping
    public List<FoodItemDTOResponse> getAllFoodItems() {
        return foodItemService.getAllFoodItems();
    }

    @GetMapping("/my-items")
    public List<FoodItemDTOResponse> getMyFoodItems(Principal principal) {
        return foodItemService.getMyFoodItems(principal);
    }

    @PostMapping
    public FoodItemDTOResponse addFoodItem(@RequestPart("form") FoodItemDTORequest foodItemDTORequest, @RequestPart(value = "photo", required = false) MultipartFile photo, Principal principal) {
        FoodItemDTOResponse foodItemDTOResponse = foodItemService.addFoodItem(foodItemDTORequest, photo, principal);
        radarService.checkAllRadarsOnFoodItemAndNotifyUsers(foodItemDTOResponse);
        return foodItemDTOResponse;
    }

    @GetMapping("/{id}")
    public FoodItemDTOResponse getFoodItemById(@PathVariable String id) {
        return foodItemService.getFoodItemById(id);
    }

    @PutMapping("/{id}")
    public FoodItemDTOResponse updateFoodItemById(@PathVariable String id, @RequestPart("form") FoodItemDTORequest foodItemDTORequest, @RequestPart(value = "photo", required = false) MultipartFile photo, Principal principal) {
        return foodItemService.updateFoodItemById(id, foodItemDTORequest, photo, principal);
    }

    @DeleteMapping("/{id}/photo")
    public String deletePhotoFromFoodItem(@PathVariable String id, Principal principal) {
        return foodItemService.deletePhotoFromFoodItem(id, principal);
    }

    @DeleteMapping("/{id}")
    public FoodItemDTOResponse deleteFoodItemById(@PathVariable String id, Principal principal) {
        return foodItemService.deleteFoodItemById(id, principal);
    }
}
