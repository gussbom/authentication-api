package com.gussbom.auth.exceptions;

import com.gussbom.auth.dtos.responses.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GenericResponse> badRequestException(BadRequestException exception) {
        return  ResponseEntity.badRequest().body(GenericResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .time(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST)
                .build());
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<GenericResponse> resourceExistsException(ResourceExistsException exception) {
        return  ResponseEntity.badRequest().body(GenericResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .time(LocalDateTime.now().toString())
                .status(HttpStatus.CONFLICT)
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse> resourceNotFoundException(ResourceNotFoundException exception) {
        return  ResponseEntity.badRequest().body(GenericResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .time(LocalDateTime.now().toString())
                .status(HttpStatus.NOT_FOUND)
                .build());
    }
}
