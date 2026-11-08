package com.example.university.controller;

import com.example.university.dto.PersonResponse;
import com.example.university.exception.ConflictException;
import com.example.university.form.PersonForm;
import com.example.university.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin pages for students: list, add, edit, delete. Only ADMIN (see SecurityConfig: /admin/**). */
@Controller
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private static final String BASE = "/admin/students";

    private final StudentService studentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Students");
        model.addAttribute("singular", "student");
        model.addAttribute("basePath", BASE);
        model.addAttribute("people", studentService.findAll().stream().map(s -> PersonResponse.of(s)).toList());
        return "admin/people";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PersonForm());
        return showForm(model, null);
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") PersonForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        form.validateLogin(result);
        if (result.hasErrors()) {
            return showForm(model, null);
        }
        try {
            studentService.create(form.toCreateRequest());
        } catch (ConflictException e) {
            result.rejectValue("username", "taken", e.getMessage());
            return showForm(model, null);
        }
        redirect.addFlashAttribute("message", "Student created.");
        return "redirect:" + BASE;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", PersonForm.from(PersonResponse.of(studentService.findById(id))));
        return showForm(model, id);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") PersonForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model, id);
        }
        studentService.update(id, form.toUpdateRequest());
        redirect.addFlashAttribute("message", "Student saved.");
        return "redirect:" + BASE;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        studentService.delete(id);
        redirect.addFlashAttribute("message", "Student deleted.");
        return "redirect:" + BASE;
    }

    private String showForm(Model model, Long id) {
        model.addAttribute("title", id == null ? "New student" : "Edit student");
        model.addAttribute("action", id == null ? BASE + "/new" : BASE + "/" + id + "/edit");
        model.addAttribute("creating", id == null);
        model.addAttribute("backUrl", BASE);
        return "admin/person-form";
    }
}
