package com.example.university.controller;

import com.example.university.dto.PersonResponse;
import com.example.university.exception.ConflictException;
import com.example.university.form.PersonForm;
import com.example.university.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin pages for teachers: list, add, edit, delete. Only ADMIN (see SecurityConfig: /admin/**). */
@Controller
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private static final String BASE = "/admin/teachers";

    private final TeacherService teacherService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Teachers");
        model.addAttribute("singular", "teacher");
        model.addAttribute("basePath", BASE);
        model.addAttribute("people", teacherService.findAll().stream().map(t -> PersonResponse.of(t)).toList());
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
            teacherService.create(form.toCreateRequest());
        } catch (ConflictException e) {
            result.rejectValue("username", "taken", e.getMessage());
            return showForm(model, null);
        }
        redirect.addFlashAttribute("message", "Teacher created.");
        return "redirect:" + BASE;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", PersonForm.from(PersonResponse.of(teacherService.findById(id))));
        return showForm(model, id);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") PersonForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model, id);
        }
        teacherService.update(id, form.toUpdateRequest());
        redirect.addFlashAttribute("message", "Teacher saved.");
        return "redirect:" + BASE;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        teacherService.delete(id);
        redirect.addFlashAttribute("message", "Teacher deleted.");
        return "redirect:" + BASE;
    }

    private String showForm(Model model, Long id) {
        model.addAttribute("title", id == null ? "New teacher" : "Edit teacher");
        model.addAttribute("action", id == null ? BASE + "/new" : BASE + "/" + id + "/edit");
        model.addAttribute("creating", id == null);
        model.addAttribute("backUrl", BASE);
        return "admin/person-form";
    }
}
