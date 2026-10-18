package com.softeng.ums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softeng.ums.entity.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByUsername(String username);
}
