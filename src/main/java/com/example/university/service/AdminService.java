package com.example.university.service;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Admin;
import com.example.university.entity.Role;
import com.example.university.exception.ConflictException;
import com.example.university.exception.NotFoundException;
import com.example.university.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found"));
    }

    public Admin create(PersonRequest req) {
        Admin admin = new Admin();
        admin.setFirstName(req.firstName().trim());
        admin.setLastName(req.lastName().trim());
        admin.setEmail(UserService.cleanEmail(req.email()));
        admin.setUser(userService.newUser(req.username().trim(), req.password(), Role.ADMIN));
        return adminRepository.save(admin);
    }

    public Admin update(Long id, PersonUpdateRequest req) {
        Admin admin = findById(id);
        admin.setFirstName(req.firstName().trim());
        admin.setLastName(req.lastName().trim());
        admin.setEmail(UserService.cleanEmail(req.email()));
        return admin;
    }

    public void delete(Long id, String currentUsername) {
        Admin admin = findById(id);
        if (admin.getUser().getUsername().equals(currentUsername)) {
            throw new ConflictException("You cannot delete your own account.");
        }
        adminRepository.delete(admin); // the login is deleted too (cascade)
    }
}
