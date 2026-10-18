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

import com.softeng.ums.entity.Teacher;
import com.softeng.ums.repository.TeacherRepository;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherRepository repo;
    private final PasswordEncoder encoder;

    public TeacherController(TeacherRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping
    public List<Teacher> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> get(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Teacher> create(@RequestBody Teacher teacher) {
        teacher.setId(null);
        teacher.setRole("ROLE_TEACHER");
        teacher.setPassword(encoder.encode(teacher.getPassword()));
        Teacher saved = repo.save(teacher);
        return ResponseEntity.created(URI.create("/api/teachers/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        return repo.findById(id).map(existing -> {
            existing.setName(teacher.getName());
            existing.setEmail(teacher.getEmail());
            existing.setUsername(teacher.getUsername());
            if (teacher.getPassword() != null && !teacher.getPassword().isBlank()) {
                existing.setPassword(encoder.encode(teacher.getPassword()));
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
