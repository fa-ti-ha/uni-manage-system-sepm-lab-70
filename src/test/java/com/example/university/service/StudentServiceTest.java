package com.example.university.service;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.AppUser;
import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.exception.NotFoundException;
import com.example.university.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT tests for StudentService.
 * StudentRepository and UserService are mocked, so only StudentService's own logic is tested.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private StudentService studentService;

    // Unit test 4
    @Test
    void findById_shouldThrowNotFound_whenStudentDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> studentService.findById(99L));

        assertEquals("Student not found", ex.getMessage());
    }

    // Unit test 5
    @Test
    void create_shouldSaveStudentWithStudentLogin() {
        AppUser login = new AppUser();
        login.setUsername("nusrat");
        login.setRole(Role.STUDENT);

        when(userService.newUser("nusrat", "nusrat123", Role.STUDENT)).thenReturn(login);
        when(studentRepository.save(any(Student.class))).thenAnswer(call -> call.getArgument(0));

        Student saved = studentService.create(
                new PersonRequest("  Nusrat ", "Jahan", "Nusrat@Uni.EDU", " nusrat ", "nusrat123"));

        assertEquals("Nusrat", saved.getFirstName());          // spaces trimmed
        assertEquals("Jahan", saved.getLastName());
        assertEquals("nusrat@uni.edu", saved.getEmail());      // email lower-cased
        assertSame(login, saved.getUser());                    // linked to its login account
        verify(userService).newUser("nusrat", "nusrat123", Role.STUDENT);
        verify(studentRepository).save(saved);
    }

    // Unit test 6
    @Test
    void update_shouldChangeNameAndEmail() {
        Student existing = new Student();
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@uni.edu");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));

        Student updated = studentService.update(1L, new PersonUpdateRequest("Rafi", "Ahmed", "   "));

        assertSame(existing, updated);
        assertEquals("Rafi", updated.getFirstName());
        assertEquals("Ahmed", updated.getLastName());
        assertNull(updated.getEmail());                        // empty email is stored as null
    }
}
