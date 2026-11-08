package com.example.university.dto;

import com.example.university.entity.Admin;
import com.example.university.entity.Student;
import com.example.university.entity.Teacher;

/** JSON returned to the client. The password is never included. */
public record PersonResponse(Long id, String firstName, String lastName, String email, String username) {

    public static PersonResponse of(Admin a) {
        return new PersonResponse(a.getId(), a.getFirstName(), a.getLastName(), a.getEmail(), a.getUser().getUsername());
    }

    public static PersonResponse of(Teacher t) {
        return new PersonResponse(t.getId(), t.getFirstName(), t.getLastName(), t.getEmail(), t.getUser().getUsername());
    }

    public static PersonResponse of(Student s) {
        return new PersonResponse(s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getUser().getUsername());
    }
}
