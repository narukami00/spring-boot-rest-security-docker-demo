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

import com.softeng.ums.entity.Student;
import com.softeng.ums.repository.StudentRepository;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repo;
    private final PasswordEncoder encoder;

    public StudentController(StudentRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping
    public List<Student> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        student.setId(null);
        student.setRole("ROLE_STUDENT");
        student.setPassword(encoder.encode(student.getPassword()));
        Student saved = repo.save(student);
        return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return repo.findById(id).map(existing -> {
            existing.setName(student.getName());
            existing.setEmail(student.getEmail());
            existing.setUsername(student.getUsername());
            if (student.getPassword() != null && !student.getPassword().isBlank()) {
                existing.setPassword(encoder.encode(student.getPassword()));
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
