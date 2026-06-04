package com.example.demo.exceptions;

public class UserInputException extends RuntimeException {
    public UserInputException(String message) {
        super(message);
    }
}