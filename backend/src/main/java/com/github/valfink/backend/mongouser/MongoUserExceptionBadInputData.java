package com.github.valfink.backend.mongouser;

public class MongoUserExceptionBadInputData extends RuntimeException {
    public MongoUserExceptionBadInputData() {
        super("Not all required fields provided!");
    }

    public MongoUserExceptionBadInputData(String message) {
        super(message);
    }
}
