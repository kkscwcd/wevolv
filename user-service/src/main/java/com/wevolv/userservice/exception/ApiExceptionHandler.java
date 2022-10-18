package com.wevolv.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserAlreadyExistsException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(GeneralException.class)
    public final ResponseEntity<Object> handleGeneralException(GeneralException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(GeneralException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }
    
}

