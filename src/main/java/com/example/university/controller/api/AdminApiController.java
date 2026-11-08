package com.example.university.controller.api;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonResponse;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Admin;
import com.example.university.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** /api/admins - only admins can use it. */
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;

    @GetMapping
    public List<PersonResponse> list() {
        return adminService.findAll().stream().map(a -> PersonResponse.of(a)).toList();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@PathVariable Long id) {
        return PersonResponse.of(adminService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        Admin admin = adminService.create(request);
        return ResponseEntity.created(URI.create("/api/admins/" + admin.getId()))
                .body(PersonResponse.of(admin));
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request) {
        return PersonResponse.of(adminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        adminService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
