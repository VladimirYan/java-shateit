package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(EntityNotFoundException e) {
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

    @ExceptionHandler(IncorrectDataException.class)
    public ResponseEntity<ApiError> handleGatewayHeaderException(IncorrectDataException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    public ResponseEntity<ApiError> handleUnsupportedStateException(UnsupportedStatusException e) {
        ApiError apiError = new ApiError("Unknown state: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingAccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(BookingAccessDeniedException e) {
        ApiError apiError = new ApiError(e.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception e) {
        ApiError apiError = new ApiError("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
