package com.example.university.service;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.exception.NotFoundException;
import com.example.university.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    @Transactional(readOnly = true)
    public Student findByUsername(String username) {
        return studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    public Student create(PersonRequest req) {
        Student student = new Student();
        student.setFirstName(req.firstName().trim());
        student.setLastName(req.lastName().trim());
        student.setEmail(UserService.cleanEmail(req.email()));
        student.setUser(userService.newUser(req.username().trim(), req.password(), Role.STUDENT));
        return studentRepository.save(student);
    }

    public Student update(Long id, PersonUpdateRequest req) {
        Student student = findById(id);
        student.setFirstName(req.firstName().trim());
        student.setLastName(req.lastName().trim());
        student.setEmail(UserService.cleanEmail(req.email()));
        return student;
    }

    public void delete(Long id) {
        studentRepository.delete(findById(id)); // the login is deleted too (cascade)
    }
}
