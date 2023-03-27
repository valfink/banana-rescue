package com.github.valfink.backend.util;

import com.github.valfink.backend.fooditem.*;
import com.github.valfink.backend.mongouser.MongoUserExceptionBadInputData;
import com.github.valfink.backend.mongouser.MongoUserExceptionNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Map<String, Object> createResponseBody(String errorMessage) {
        Map<String, Object> responseBody = new LinkedHashMap<>();

        responseBody.put("timestamp", Instant.now());
        responseBody.put("error", errorMessage);

        return responseBody;
    }

    @ExceptionHandler(FoodItemExceptionAuthorization.class)
    public ResponseEntity handleFoodItemExceptionAuthorization(FoodItemExceptionAuthorization exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FoodItemExceptionBadInputData.class)
    public ResponseEntity handleFoodItemExceptionBadInputData(FoodItemExceptionBadInputData exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FoodItemExceptionDataMismatch.class)
    public ResponseEntity handleFoodItemExceptionDataMismatch(FoodItemExceptionDataMismatch exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FoodItemExceptionNotFound.class)
    public ResponseEntity handleFoodItemExceptionNotFound(FoodItemExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodItemExceptionPhotoAction.class)
    public ResponseEntity handleFoodItemExceptionPhotoAction(FoodItemExceptionPhotoAction exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MongoUserExceptionBadInputData.class)
    public ResponseEntity handleMongoUserExceptionBadInputData(MongoUserExceptionBadInputData exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MongoUserExceptionNotFound.class)
    public ResponseEntity handleMongoUserExceptionNotFound(MongoUserExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity(responseBody, HttpStatus.NOT_FOUND);
    }
}