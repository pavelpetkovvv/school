package com.school.management.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class StudentDTO {
    private UUID id;
    private String name;
    private Integer age;
    private String group;
    private List<CourseDTO> courses;
}
