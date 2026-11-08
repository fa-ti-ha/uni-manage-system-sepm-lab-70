package com.example.university.controller;

import com.example.university.exception.ConflictException;
import com.example.university.form.PasswordForm;
import com.example.university.service.UserService;
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

/** "Change password" page for every logged-in user (any role). */
@Controller
@RequestMapping("/account/password")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", new PasswordForm());
        return "password";
    }

    @PostMapping
    public String change(@Valid @ModelAttribute("form") PasswordForm form, BindingResult result,
                         Authentication auth, RedirectAttributes redirect) {
        if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmPassword())
                && !result.hasFieldErrors("confirmPassword")) {
            result.rejectValue("confirmPassword", "mismatch", "The two new passwords do not match");
        }
        if (result.hasErrors()) {
            return "password";
        }
        try {
            userService.changePassword(auth.getName(), form.getCurrentPassword(), form.getNewPassword());
        } catch (ConflictException e) {
            result.rejectValue("currentPassword", "wrong", e.getMessage());
            return "password";
        }
        redirect.addFlashAttribute("message", "Password changed.");
        return "redirect:/account/password";
    }
}
