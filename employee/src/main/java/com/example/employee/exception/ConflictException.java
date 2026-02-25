package com.example.employee.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}