package com.example.university.controller.api;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonResponse;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Student;
import com.example.university.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * /api/students
 * Admin: everything. Teacher: read all students. Student: read and update only their own record.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentApiController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public List<PersonResponse> list() {
        return studentService.findAll().stream().map(s -> PersonResponse.of(s)).toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public PersonResponse me(Authentication auth) {
        return PersonResponse.of(studentService.findByUsername(auth.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public PersonResponse get(@PathVariable Long id, Authentication auth) {
        Student student = studentService.findById(id);
        ApiAccess.requireStaffOrOwner(auth, student.getUser().getUsername());
        return PersonResponse.of(student);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        Student student = studentService.create(request);
        return ResponseEntity.created(URI.create("/api/students/" + student.getId()))
                .body(PersonResponse.of(student));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request,
                                 Authentication auth) {
        Student student = studentService.findById(id);
        ApiAccess.requireAdminOrOwner(auth, student.getUser().getUsername());
        return PersonResponse.of(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
