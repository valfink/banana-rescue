package com.github.valfink.backend.fooditem;

public class FoodItemExceptionNotFound extends RuntimeException {
    public FoodItemExceptionNotFound() {
        super("The requested food item could not be found!");
    }

    public FoodItemExceptionNotFound(String message) {
        super(message);
    }
}
