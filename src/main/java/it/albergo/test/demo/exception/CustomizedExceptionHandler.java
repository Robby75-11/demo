package it.albergo.test.demo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomizedExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public it.albergo.test.demo.exception.ApiError notFoundExceptionHandler(NotFoundException e){
        it.albergo.test.demo.exception.ApiError error = new it.albergo.test.demo.exception.ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return error;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public it.albergo.test.demo.exception.ApiError validationExceptionHandler(ValidationException e){
        it.albergo.test.demo.exception.ApiError error = new it.albergo.test.demo.exception.ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return error;
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public it.albergo.test.demo.exception.ApiError unauthorizedExceptionHandler(UnAuthorizedException e) {
        it.albergo.test.demo.exception.ApiError error = new it.albergo.test.demo.exception.ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        return error;
    }

}
