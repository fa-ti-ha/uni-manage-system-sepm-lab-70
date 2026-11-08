package com.example.university.integration;

import com.example.university.dto.PersonRequest;
import com.example.university.service.AdminService;
import com.example.university.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * INTEGRATION tests for the REST API + Spring Security + PostgreSQL together.
 * MockMvc sends real HTTP requests (with HTTP Basic login) through the security filters,
 * the controllers, the services and the database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    // Integration test 2
    @Test
    void admin_canListStudents_throughRestApi() throws Exception {
        adminService.create(new PersonRequest("Test", "Admin", null, "it_admin", "admin123"));
        studentService.create(new PersonRequest("Mim", "Akter", "mim@uni.edu", "it_mim", "mim12345"));

        mockMvc.perform(get("/api/students").with(httpBasic("it_admin", "admin123")))
                .andExpect(status().isOk())                                      // 200
                .andExpect(jsonPath("$[*].username", hasItem("it_mim")));        // JSON contains the student
    }

    // Integration test 3
    @Test
    void student_isForbiddenFromAdminApi() throws Exception {
        studentService.create(new PersonRequest("Sakib", "Hasan", null, "it_sakib", "sakib123"));

        mockMvc.perform(get("/api/admins").with(httpBasic("it_sakib", "sakib123")))
                .andExpect(status().isForbidden());                              // 403: role-based access control
    }
}
