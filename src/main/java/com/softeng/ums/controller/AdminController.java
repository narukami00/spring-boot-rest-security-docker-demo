package com.softeng.ums.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Admin> get(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<Admin> update(@PathVariable Long id, @RequestBody Admin admin) {
        return repo.findById(id).map(existing -> {
            existing.setName(admin.getName());
            existing.setEmail(admin.getEmail());
            existing.setUsername(admin.getUsername());
            if (admin.getPassword() != null && !admin.getPassword().isBlank()) {
                existing.setPassword(encoder.encode(admin.getPassword()));
            }
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
