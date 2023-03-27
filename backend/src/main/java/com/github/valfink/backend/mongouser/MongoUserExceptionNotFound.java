package com.github.valfink.backend.mongouser;

public class MongoUserExceptionNotFound extends RuntimeException {
    static String reasonUsername(String username) {
        return "The user " + username + " doesn't exist in the database.";
    }

    static String reasonId(String id) {
        return "The user with the id " + id + " doesn't exist in the database.";
    }

    public MongoUserExceptionNotFound(String message) {
        super(message);
    }
}
