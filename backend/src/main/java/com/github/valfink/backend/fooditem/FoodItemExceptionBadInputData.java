package com.github.valfink.backend.fooditem;

public class FoodItemExceptionBadInputData extends RuntimeException {
    public FoodItemExceptionBadInputData() {
        super("Not all required fields provided!");
    }

    public FoodItemExceptionBadInputData(String message) {
        super(message);
    }
}
