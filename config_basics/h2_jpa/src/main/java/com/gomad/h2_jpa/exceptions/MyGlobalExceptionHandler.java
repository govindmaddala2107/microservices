package com.gomad.h2_jpa.exceptions;

import com.gomad.h2_jpa.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err -> {
            String key = ((FieldError)err).getField();
            String msg = err.getDefaultMessage();

            errors.put(key, msg);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> myResourceNotFoundException(ResourceNotFoundException e){
        ApiResponse apiResponse = new ApiResponse(e.getMessage(), false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ApiResponse> myAPIException(APIException e){
        ApiResponse apiResponse = new ApiResponse(e.getMessage(), false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
