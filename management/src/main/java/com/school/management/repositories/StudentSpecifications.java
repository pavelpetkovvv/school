package com.school.management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.school.management.models.Student;

import jakarta.persistence.criteria.Join;

public class StudentSpecifications {

    public static Specification<Student> hasCourseId(UUID courseId) {
        return (root, query, cb) -> {
            Join<Object, Object> courseJoin = root.join("courses");
            return cb.equal(courseJoin.get("id"), courseId);
        };
    }

    public static Specification<Student> hasGroup(String group) {
        return (root, query, cb) -> cb.equal(root.get("group"), group);
    }

    public static Specification<Student> hasName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }

    public static Specification<Student> isOlderThan(int age) {
        return (root, query, cb) -> cb.greaterThan(root.get("age"), age);
    }
}