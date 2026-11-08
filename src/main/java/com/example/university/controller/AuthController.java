package com.example.university.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /** Our own login page (templates/login.html). Spring Security handles the POST /login itself. */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** After login, send each role to its own page. */
    @GetMapping("/")
    public String home(Authentication auth) {
        if (hasRole(auth, "ADMIN")) return "redirect:/admin/students";
        if (hasRole(auth, "TEACHER")) return "redirect:/teacher/profile";
        return "redirect:/student/profile";
    }

    private static boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
