package com.softeng.ums.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softeng.ums.entity.Admin;
import com.softeng.ums.errors.ApiError;
import com.softeng.ums.repository.AdminRepository;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminRepository repo;
    private final PasswordEncoder encoder;

    public AdminController(AdminRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping
    public List<Admin> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Admin> found = repo.findById(id);
        if (found.isEmpty()) {
            return notFound("Admin " + id + " does not exist");
        }
        return ResponseEntity.ok(found.get());
    }

    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody Admin admin) {
        admin.setId(null);
        admin.setRole("ROLE_ADMIN");
        admin.setPassword(encoder.encode(admin.getPassword()));
        Admin saved = repo.save(admin);
        return ResponseEntity.created(URI.create("/api/admins/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Admin admin) {
        Optional<Admin> found = repo.findById(id);
        if (found.isEmpty()) {
            return notFound("Admin " + id + " does not exist");
        }

        Admin existing = found.get();
        existing.setName(admin.getName());
        existing.setEmail(admin.getEmail());
        existing.setUsername(admin.getUsername());
        // Password is only replaced when the request supplies one. A null would
        // otherwise overwrite a working hash and lock the account out permanently.
        if (admin.getPassword() != null && !admin.getPassword().isBlank()) {
            existing.setPassword(encoder.encode(admin.getPassword()));
        }
        return ResponseEntity.ok(repo.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        Optional<Admin> found = repo.findById(id);
        if (found.isEmpty()) {
            return notFound("Admin " + id + " does not exist");
        }
        // Deleting your own record would leave a live session pointing at a user
        // that no longer exists. Refuse it rather than corrupting the login.
        if (auth != null && found.get().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiError.forbidden("You cannot delete your own account"));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ApiError> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.notFound(message));
    }
}
