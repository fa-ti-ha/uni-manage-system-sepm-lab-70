package com.example.university.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Becomes HTTP 404. */
@ResponseStatus(HttpStatus.NOT_FOUND) // status for the website's error page
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
