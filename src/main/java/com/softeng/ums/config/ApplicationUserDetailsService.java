package com.softeng.ums.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.softeng.ums.entity.Admin;
import com.softeng.ums.entity.Student;
import com.softeng.ums.entity.Teacher;
import com.softeng.ums.repository.AdminRepository;
import com.softeng.ums.repository.StudentRepository;
import com.softeng.ums.repository.TeacherRepository;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;

    public ApplicationUserDetailsService(AdminRepository adminRepo, TeacherRepository teacherRepo, StudentRepository studentRepo) {
        this.adminRepo = adminRepo;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByUsername(username);
        if (admin != null) {
            return toUserDetails(admin.getUsername(), admin.getPassword(), admin.getRole());
        }

        Teacher teacher = teacherRepo.findByUsername(username);
        if (teacher != null) {
            return toUserDetails(teacher.getUsername(), teacher.getPassword(), teacher.getRole());
        }

        Student student = studentRepo.findByUsername(username);
        if (student != null) {
            return toUserDetails(student.getUsername(), student.getPassword(), student.getRole());
        }

        throw new UsernameNotFoundException("No user found with username: " + username);
    }

    private UserDetails toUserDetails(String username, String password, String role) {
        return User.withUsername(username)
                .password(password)
                .authorities(new SimpleGrantedAuthority(role))
                .build();
    }
}