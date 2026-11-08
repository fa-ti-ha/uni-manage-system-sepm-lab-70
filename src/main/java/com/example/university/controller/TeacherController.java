package com.example.university.controller;

import com.example.university.dto.PersonResponse;
import com.example.university.entity.Teacher;
import com.example.university.form.PersonForm;
import com.example.university.service.TeacherService;
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

/** Teacher pages: view and edit their own info. Only TEACHER (see SecurityConfig: /teacher/**). */
@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Teacher teacher = teacherService.findByUsername(auth.getName());
        model.addAttribute("form", PersonForm.from(PersonResponse.of(teacher)));
        return showProfile(model);
    }

    @PostMapping("/profile")
    public String save(@Valid @ModelAttribute("form") PersonForm form, BindingResult result,
                       Authentication auth, Model model, RedirectAttributes redirect) {
        // Always the logged-in teacher's own record: they cannot edit anyone else.
        Teacher teacher = teacherService.findByUsername(auth.getName());
        form.setUsername(teacher.getUser().getUsername());
        if (result.hasErrors()) {
            return showProfile(model);
        }
        teacherService.update(teacher.getId(), form.toUpdateRequest());
        redirect.addFlashAttribute("message", "Profile saved.");
        return "redirect:/teacher/profile";
    }

    private String showProfile(Model model) {
        model.addAttribute("roleLabel", "Teacher");
        model.addAttribute("action", "/teacher/profile");
        return "profile";
    }
}
