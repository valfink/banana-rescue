package com.github.valfink.backend.fooditem;

public class FoodItemExceptionPhotoAction extends RuntimeException {
    public FoodItemExceptionPhotoAction() {
        super("The photo related action didn't work!");
    }

    public FoodItemExceptionPhotoAction(String message) {
        super(message);
    }
}
