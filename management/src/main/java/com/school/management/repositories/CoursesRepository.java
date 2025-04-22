package com.school.management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.management.models.Course;

public interface CoursesRepository extends JpaRepository<Course, UUID> {

}
