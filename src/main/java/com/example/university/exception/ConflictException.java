package com.example.university.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Becomes HTTP 409 (username taken, wrong current password, deleting yourself, ...). */
@ResponseStatus(HttpStatus.CONFLICT) // status for the website's error page
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
