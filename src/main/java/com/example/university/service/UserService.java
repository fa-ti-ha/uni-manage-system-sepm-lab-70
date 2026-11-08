package com.example.university.service;

import com.example.university.entity.AppUser;
import com.example.university.entity.Role;
import com.example.university.exception.ConflictException;
import com.example.university.exception.NotFoundException;
import com.example.university.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Login accounts: creating them (with a BCrypt password) and changing passwords. */
@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Builds a new login. It is saved together with the admin/teacher/student (cascade). */
    public AppUser newUser(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username '" + username + "' is already taken.");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // BCrypt hash
        user.setRole(role);
        return user;
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ConflictException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    /** Empty email becomes null; otherwise trimmed and lower-case. */
    static String cleanEmail(String email) {
        return (email == null || email.isBlank()) ? null : email.trim().toLowerCase();
    }
}
