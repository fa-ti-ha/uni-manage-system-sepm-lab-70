package com.example.university.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Data typed into the "Change password" form. */
@Getter
@Setter
public class PasswordForm {

    @NotBlank(message = "Enter your current password")
    private String currentPassword;

    @NotBlank(message = "Enter a new password")
    @Size(min = 6, max = 72, message = "New password must be 6-72 characters")
    private String newPassword;

    @NotBlank(message = "Repeat the new password")
    private String confirmPassword;
}
