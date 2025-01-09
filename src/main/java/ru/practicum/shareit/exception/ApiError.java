package ru.practicum.shareit.exception;

public class ApiError {
    private final String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

