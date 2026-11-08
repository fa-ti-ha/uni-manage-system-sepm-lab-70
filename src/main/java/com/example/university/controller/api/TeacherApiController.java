package com.example.university.controller.api;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonResponse;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Teacher;
import com.example.university.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * /api/teachers
 * Admin: everything. Teacher: read and update only their own record.
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherApiController {

    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PersonResponse> list() {
        return teacherService.findAll().stream().map(t -> PersonResponse.of(t)).toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public PersonResponse me(Authentication auth) {
        return PersonResponse.of(teacherService.findByUsername(auth.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public PersonResponse get(@PathVariable Long id, Authentication auth) {
        Teacher teacher = teacherService.findById(id);
        ApiAccess.requireAdminOrOwner(auth, teacher.getUser().getUsername());
        return PersonResponse.of(teacher);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        Teacher teacher = teacherService.create(request);
        return ResponseEntity.created(URI.create("/api/teachers/" + teacher.getId()))
                .body(PersonResponse.of(teacher));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request,
                                 Authentication auth) {
        Teacher teacher = teacherService.findById(id);
        ApiAccess.requireAdminOrOwner(auth, teacher.getUser().getUsername());
        return PersonResponse.of(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
