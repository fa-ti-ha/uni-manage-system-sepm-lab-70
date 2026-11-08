package com.example.university.service;

import com.example.university.dto.PersonRequest;
import com.example.university.dto.PersonUpdateRequest;
import com.example.university.entity.Role;
import com.example.university.entity.Teacher;
import com.example.university.exception.NotFoundException;
import com.example.university.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher not found"));
    }

    @Transactional(readOnly = true)
    public Teacher findByUsername(String username) {
        return teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found"));
    }

    public Teacher create(PersonRequest req) {
        Teacher teacher = new Teacher();
        teacher.setFirstName(req.firstName().trim());
        teacher.setLastName(req.lastName().trim());
        teacher.setEmail(UserService.cleanEmail(req.email()));
        teacher.setUser(userService.newUser(req.username().trim(), req.password(), Role.TEACHER));
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, PersonUpdateRequest req) {
        Teacher teacher = findById(id);
        teacher.setFirstName(req.firstName().trim());
        teacher.setLastName(req.lastName().trim());
        teacher.setEmail(UserService.cleanEmail(req.email()));
        return teacher;
    }

    public void delete(Long id) {
        teacherRepository.delete(findById(id)); // the login is deleted too (cascade)
    }
}
