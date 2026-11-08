package com.example.university.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Data for creating an admin, teacher or student together with their login (REST API and web form). */
public record PersonRequest(
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @Email @Size(max = 120) String email,

        @NotBlank @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "may only contain letters, digits, dot, underscore and dash")
        String username,

        @NotBlank @Size(min = 6, max = 72)
        String password) {
}
