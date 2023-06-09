package com.github.valfink.backend.util;

import com.github.valfink.backend.chat.ChatExceptionAuthorization;
import com.github.valfink.backend.chat.ChatExceptionNotFound;
import com.github.valfink.backend.fooditem.*;
import com.github.valfink.backend.mongouser.MongoUserExceptionBadInputData;
import com.github.valfink.backend.mongouser.MongoUserExceptionNotFound;
import com.github.valfink.backend.radar.RadarExceptionBadInputData;
import com.github.valfink.backend.radar.RadarExceptionNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final TimestampService timestampService;

    private Map<String, Object> createResponseBody(String errorMessage) {
        Map<String, Object> responseBody = new LinkedHashMap<>();

        responseBody.put("timestamp", timestampService.generateTimestamp());
        responseBody.put("error", errorMessage);

        return responseBody;
    }

    @ExceptionHandler(FoodItemExceptionAuthorization.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemExceptionAuthorization(FoodItemExceptionAuthorization exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FoodItemExceptionBadInputData.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemExceptionBadInputData(FoodItemExceptionBadInputData exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FoodItemExceptionDataMismatch.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemExceptionDataMismatch(FoodItemExceptionDataMismatch exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FoodItemExceptionNotFound.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemExceptionNotFound(FoodItemExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodItemExceptionPhotoAction.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemExceptionPhotoAction(FoodItemExceptionPhotoAction exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MongoUserExceptionBadInputData.class)
    public ResponseEntity<Map<String, Object>> handleMongoUserExceptionBadInputData(MongoUserExceptionBadInputData exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MongoUserExceptionNotFound.class)
    public ResponseEntity<Map<String, Object>> handleMongoUserExceptionNotFound(MongoUserExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChatExceptionAuthorization.class)
    public ResponseEntity<Map<String, Object>> handleChatExceptionAuthorization(ChatExceptionAuthorization exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ChatExceptionNotFound.class)
    public ResponseEntity<Map<String, Object>> handleChatExceptionNotFound(ChatExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RadarExceptionBadInputData.class)
    public ResponseEntity<Map<String, Object>> handleRadarExceptionBadInputData(RadarExceptionBadInputData exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RadarExceptionNotFound.class)
    public ResponseEntity<Map<String, Object>> handleRadarExceptionNotFound(RadarExceptionNotFound exception) {
        Map<String, Object> responseBody = createResponseBody(exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }
}
