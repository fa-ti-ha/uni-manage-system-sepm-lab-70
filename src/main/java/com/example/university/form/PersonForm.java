package com.example.university.form;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonResponse;
import com.example.university.dto.PersonUpdateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;

/** Data typed into the admin / teacher / student form on the website. */
@Getter
@Setter
@NoArgsConstructor
public class PersonForm {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "At most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "At most 50 characters")
    private String lastName;

    @Email(message = "Not a valid email address")
    @Size(max = 120, message = "At most 120 characters")
    private String email;

    // Only used when creating a new person (checked in validateLogin)
    private String username;
    private String password;

    /** Fills the form with an existing person (for "Edit" and "My profile"). */
    public static PersonForm from(PersonResponse p) {
        PersonForm form = new PersonForm();
        form.setFirstName(p.firstName());
        form.setLastName(p.lastName());
        form.setEmail(p.email());
        form.setUsername(p.username());
        return form;
    }

    /** Extra checks when creating: username and password are required. */
    public void validateLogin(BindingResult result) {
        String u = username == null ? "" : username.trim();
        if (!u.matches("^[a-zA-Z0-9._-]{3,50}$")) {
            result.rejectValue("username", "invalid",
                    "3-50 characters: letters, digits, dot, underscore or dash");
        }
        if (password == null || password.length() < 6 || password.length() > 72) {
            result.rejectValue("password", "invalid", "Password must be 6-72 characters");
        }
    }

    public PersonRequest toCreateRequest() {
        return new PersonRequest(firstName, lastName, email, username.trim(), password);
    }

    public PersonUpdateRequest toUpdateRequest() {
        return new PersonUpdateRequest(firstName, lastName, email);
    }
}
