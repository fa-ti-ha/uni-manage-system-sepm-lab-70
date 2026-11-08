package com.example.university.config;

import com.example.university.dto.PersonRequest;
import com.example.university.repository.AppUserRepository;
import com.example.university.service.AdminService;
import com.example.university.service.StudentService;
import com.example.university.service.TeacherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Creates the first accounts when they don't exist yet. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createFirstUsers(AdminService adminService, TeacherService teacherService,
                                       StudentService studentService, AppUserRepository userRepository,
                                       @Value("${app.admin.username}") String adminUsername,
                                       @Value("${app.admin.password}") String adminPassword,
                                       @Value("${app.demo-users:true}") boolean demoUsers) {
        return args -> {
            if (!userRepository.existsByUsername(adminUsername)) {
                adminService.create(new PersonRequest("System", "Admin", null, adminUsername, adminPassword));
            }
            if (demoUsers && !userRepository.existsByUsername("teacher1")) {
                teacherService.create(new PersonRequest("Ayesha", "Rahman", "ayesha@uni.edu", "teacher1", "teacher123"));
            }
            if (demoUsers && !userRepository.existsByUsername("student1")) {
                studentService.create(new PersonRequest("Rafi", "Ahmed", "rafi@uni.edu", "student1", "student123"));
            }
        };
    }
}
