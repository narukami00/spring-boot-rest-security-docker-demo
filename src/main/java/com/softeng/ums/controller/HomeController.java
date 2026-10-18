package com.softeng.ums.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        String username = "unknown";
        boolean isAdmin = false;
        boolean isTeacher = false;
        boolean isStudent = false;

        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
            isAdmin = hasRole(authentication, "ROLE_ADMIN");
            isTeacher = hasRole(authentication, "ROLE_TEACHER");
            isStudent = hasRole(authentication, "ROLE_STUDENT");
        }

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);

        return "home";
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}