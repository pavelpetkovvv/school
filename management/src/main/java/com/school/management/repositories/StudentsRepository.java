package com.school.management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.school.management.models.Student;

public interface StudentsRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
}
