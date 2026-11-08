package com.example.university.service;

import com.example.university.entity.AppUser;
import com.example.university.entity.Role;
import com.example.university.exception.ConflictException;
import com.example.university.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT tests for UserService.
 * No database and no Spring: the repository and the password encoder are fake objects (Mockito mocks).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // Unit test 1
    @Test
    void newUser_shouldHashPasswordAndSetRole() {
        when(userRepository.existsByUsername("rafi")).thenReturn(false);
        when(passwordEncoder.encode("rafi123")).thenReturn("$2a$10$hashedValue");

        AppUser user = userService.newUser("rafi", "rafi123", Role.STUDENT);

        assertEquals("rafi", user.getUsername());
        assertEquals("$2a$10$hashedValue", user.getPassword());   // the hash is stored ...
        assertNotEquals("rafi123", user.getPassword());           // ... never the plain password
        assertEquals(Role.STUDENT, user.getRole());
        verify(passwordEncoder).encode("rafi123");
    }

    // Unit test 2
    @Test
    void newUser_shouldThrowConflict_whenUsernameIsTaken() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> userService.newUser("admin", "admin123", Role.ADMIN));

        verify(passwordEncoder, never()).encode(any());
    }

    // Unit test 3
    @Test
    void changePassword_shouldThrowConflict_whenCurrentPasswordIsWrong() {
        AppUser user = new AppUser();
        user.setUsername("student1");
        user.setPassword("$2a$10$oldHash");

        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$oldHash")).thenReturn(false);

        assertThrows(ConflictException.class,
                () -> userService.changePassword("student1", "wrongPassword", "newPass123"));

        assertEquals("$2a$10$oldHash", user.getPassword());   // password was NOT changed
        verify(passwordEncoder, never()).encode(any());
    }
}
