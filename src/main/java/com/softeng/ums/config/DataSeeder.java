package com.softeng.ums.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.softeng.ums.entity.Admin;
import com.softeng.ums.entity.Student;
import com.softeng.ums.entity.Teacher;
import com.softeng.ums.repository.AdminRepository;
import com.softeng.ums.repository.StudentRepository;
import com.softeng.ums.repository.TeacherRepository;

@Configuration
public class DataSeeder {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner seed(AdminRepository adminRepo, TeacherRepository teacherRepo, StudentRepository studentRepo, PasswordEncoder encoder) {
        return args -> {
            if (adminRepo.count() == 0) {
                adminRepo.save(new Admin("Admin User", "admin@uni.com", "admin", encoder.encode("admin123"), "ROLE_ADMIN"));
            }
            if (teacherRepo.count() == 0) {
                teacherRepo.save(new Teacher("Teacher User", "teacher@uni.com", "teacher", encoder.encode("teacher123"), "ROLE_TEACHER"));
            }
            if (studentRepo.count() == 0) {
                studentRepo.save(new Student("Student User", "student@uni.com", "student", encoder.encode("student123"), "ROLE_STUDENT"));
            }
        };
    }
}
