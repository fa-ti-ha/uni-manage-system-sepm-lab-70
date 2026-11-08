package com.example.university.controller;

import com.example.university.dto.PersonResponse;
import com.example.university.entity.Student;
import com.example.university.form.PersonForm;
import com.example.university.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Student pages: view and edit their own info. Only STUDENT (see SecurityConfig: /student/**). */
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Student student = studentService.findByUsername(auth.getName());
        model.addAttribute("form", PersonForm.from(PersonResponse.of(student)));
        return showProfile(model);
    }

    @PostMapping("/profile")
    public String save(@Valid @ModelAttribute("form") PersonForm form, BindingResult result,
                       Authentication auth, Model model, RedirectAttributes redirect) {
        // Always the logged-in student's own record: they cannot edit anyone else.
        Student student = studentService.findByUsername(auth.getName());
        form.setUsername(student.getUser().getUsername());
        if (result.hasErrors()) {
            return showProfile(model);
        }
        studentService.update(student.getId(), form.toUpdateRequest());
        redirect.addFlashAttribute("message", "Profile saved.");
        return "redirect:/student/profile";
    }

    private String showProfile(Model model) {
        model.addAttribute("roleLabel", "Student");
        model.addAttribute("action", "/student/profile");
        return "profile";
    }
}
