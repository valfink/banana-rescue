package com.github.valfink.backend.fooditem;

public class FoodItemExceptionInputDataMissing extends RuntimeException {
    public FoodItemExceptionInputDataMissing() {
        super("Not all required fields provided!");
    }

    public FoodItemExceptionInputDataMissing(String message) {
        super(message);
    }
}
