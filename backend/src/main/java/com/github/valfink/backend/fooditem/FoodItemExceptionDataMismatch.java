package com.github.valfink.backend.fooditem;

public class FoodItemExceptionDataMismatch extends RuntimeException {
    public FoodItemExceptionDataMismatch() {
        super("The food item data does not work with the requested action!");
    }

    public FoodItemExceptionDataMismatch(String message) {
        super(message);
    }
}
