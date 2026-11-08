package com.example.university.dto;

/** JSON returned when something goes wrong, e.g. {"status":404,"message":"Student not found"}. */
public record ApiError(int status, String message) {
}
