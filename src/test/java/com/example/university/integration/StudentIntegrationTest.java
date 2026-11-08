package com.example.university.integration;

import com.example.university.dto.PersonRequest;
import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.repository.StudentRepository;
import com.example.university.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION test: the whole application starts and talks to a real PostgreSQL database.
 * Service -> Repository -> Hibernate -> PostgreSQL are all real (nothing is mocked).
 * {@code @Transactional} rolls every test back, so the database stays clean.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Integration test 1
    @Test
    void createStudent_shouldBeSavedInDatabaseWithBcryptPassword() {
        Student created = studentService.create(
                new PersonRequest("Tanvir", "Hasan", "tanvir@uni.edu", "it_tanvir", "tanvir123"));

        assertNotNull(created.getId());   // the database generated an id

        // Read it back from PostgreSQL by the login's username
        Student fromDb = studentRepository.findByUserUsername("it_tanvir").orElseThrow();

        assertEquals("Tanvir", fromDb.getFirstName());
        assertEquals(Role.STUDENT, fromDb.getUser().getRole());
        assertNotEquals("tanvir123", fromDb.getUser().getPassword());                    // not plain text
        assertTrue(passwordEncoder.matches("tanvir123", fromDb.getUser().getPassword())); // valid BCrypt hash
    }
}
