package com.auth.testlogin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Applicable on all other controllers
 */
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

    @ExceptionHandler(WrongUserCredentialsException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(WrongUserCredentialsException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenNotValidException.class)
    public final ResponseEntity<Object> handleTokenNotValidException(TokenNotValidException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(NotFoundException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

  /*  @ExceptionHandler(UserNotVerified.class)
    public ResponseEntity<UserNotVerifiedResponse> handleExceptionU(UserNotVerified exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new UserNotVerifiedResponse(exception.getPhoneNumber()));
    }*/

    @ExceptionHandler(UserNotVerified.class)
    public ResponseEntity<Object> handleException(UserNotVerified exception) {
        UserNotVerifiedResponse apiException =
                new UserNotVerifiedResponse(exception.getMessage(), exception.getPhoneNumber(), exception.getKeycloakId());

        return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
    }
}
