package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ApiError {

    private String message;
    private int status;
    private long timestamp;

    public ApiError() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiError(String message, int status) {
        this();
        this.message = message;
        this.status = status;
    }
}
