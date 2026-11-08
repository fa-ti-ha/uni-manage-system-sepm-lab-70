package com.example.university.controller;

import com.example.university.dto.PersonResponse;
import com.example.university.exception.ConflictException;
import com.example.university.form.PersonForm;
import com.example.university.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin pages for other admins: list, add, edit, delete (not yourself). Only ADMIN. */
@Controller
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
public class AdminAdminController {

    private static final String BASE = "/admin/admins";

    private final AdminService adminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Admins");
        model.addAttribute("singular", "admin");
        model.addAttribute("basePath", BASE);
        model.addAttribute("people", adminService.findAll().stream().map(a -> PersonResponse.of(a)).toList());
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
            adminService.create(form.toCreateRequest());
        } catch (ConflictException e) {
            result.rejectValue("username", "taken", e.getMessage());
            return showForm(model, null);
        }
        redirect.addFlashAttribute("message", "Admin created.");
        return "redirect:" + BASE;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", PersonForm.from(PersonResponse.of(adminService.findById(id))));
        return showForm(model, id);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") PersonForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model, id);
        }
        adminService.update(id, form.toUpdateRequest());
        redirect.addFlashAttribute("message", "Admin saved.");
        return "redirect:" + BASE;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes redirect) {
        try {
            adminService.delete(id, auth.getName());
            redirect.addFlashAttribute("message", "Admin deleted.");
        } catch (ConflictException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + BASE;
    }

    private String showForm(Model model, Long id) {
        model.addAttribute("title", id == null ? "New admin" : "Edit admin");
        model.addAttribute("action", id == null ? BASE + "/new" : BASE + "/" + id + "/edit");
        model.addAttribute("creating", id == null);
        model.addAttribute("backUrl", BASE);
        return "admin/person-form";
    }
}
