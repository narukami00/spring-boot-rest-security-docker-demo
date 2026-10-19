package com.softeng.ums.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.softeng.ums.repository.AdminRepository;
import com.softeng.ums.repository.StudentRepository;
import com.softeng.ums.repository.TeacherRepository;

@Controller
public class HomeController {

    private final AdminRepository adminRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;

    public HomeController(AdminRepository adminRepo, TeacherRepository teacherRepo, StudentRepository studentRepo) {
        this.adminRepo = adminRepo;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model, HttpSession session) {
        String username = "unknown";
        String displayName = "unknown";
        boolean isAdmin = false;
        boolean isTeacher = false;
        boolean isStudent = false;

        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();

            // The session outlives the database row. If the record was deleted
            // (including deleting your own account) there is nothing left to
            // authenticate against, so drop the session rather than serving a
            // dashboard for a user who no longer exists.
            if (!exists(username)) {
                session.invalidate();
                return "redirect:/login";
            }

            displayName = realName(username);
            isAdmin = hasRole(authentication, "ROLE_ADMIN");
            isTeacher = hasRole(authentication, "ROLE_TEACHER");
            isStudent = hasRole(authentication, "ROLE_STUDENT");
        }

        model.addAttribute("username", username);
        model.addAttribute("name", displayName);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);

        return "home";
    }

    /** True when the login id still resolves to a row in one of the three tables. */
    private boolean exists(String username) {
        return adminRepo.findByUsername(username) != null
                || teacherRepo.findByUsername(username) != null
                || studentRepo.findByUsername(username) != null;
    }

    /** The session only carries the login id, so the friendly name needs a lookup. */
    private String realName(String username) {
        var admin = adminRepo.findByUsername(username);
        if (admin != null) {
            return admin.getName();
        }
        var teacher = teacherRepo.findByUsername(username);
        if (teacher != null) {
            return teacher.getName();
        }
        var student = studentRepo.findByUsername(username);
        if (student != null) {
            return student.getName();
        }
        return username;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
