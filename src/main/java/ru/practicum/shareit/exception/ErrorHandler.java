package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailIsAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailIsAlreadyRegisteredException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity<ApiError> handleEmptyFieldException(EmptyFieldException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GatewayHeaderException.class)
    public ResponseEntity<ApiError> handleGatewayHeaderException(GatewayHeaderException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
