package com.school.management.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.management.dto.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ExceptionAdvisor {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(IllegalStateException exception) {
        return buildErrorResponse(exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        return buildErrorResponse(exception);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception exception) {
        return buildErrorResponse(exception);
    }

    private ErrorResponse buildErrorResponse(Throwable exception) {
        return ErrorResponse.builder()
                .error(ErrorResponse.Error.builder()
                        .message(exception.getMessage())
                        .build())
                .build();
    }
}
