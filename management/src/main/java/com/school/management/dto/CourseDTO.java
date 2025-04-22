package com.school.management.dto;

import java.util.UUID;

import com.school.management.models.CourseType;

import lombok.Data;

@Data
public class CourseDTO {
    private UUID id;
    private String name;
    private CourseType courseType;
}
