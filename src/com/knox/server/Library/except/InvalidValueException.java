package com.knox.server.Library.except;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message) {
        super("Your catalog is not in the correct format. Reason: " + message);
    }
}