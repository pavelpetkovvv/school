package com.school.management.controllers;

import static com.school.management.constants.ApiConstants.STUDENTS_URL;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.school.management.dto.PageResult;
import com.school.management.dto.StudentDTO;
import com.school.management.models.Student;
import com.school.management.services.StudentsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(STUDENTS_URL)
@Tag(name = "Students", description = "Operations related to student management")
public class StudentsController {

    private final StudentsService studentsService;

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieves a paginated list of students based on optional filter criteria.")
    public PageResult<StudentDTO> getAll(@RequestBody(required = false) Student probe,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer pageNumber) {
        return studentsService.getAll(probe, pageSize, pageNumber);
    }

    @PostMapping
    @Operation(summary = "Add a new student", description = "Creates a new student in the system.")
    public Student add(@RequestBody Student student) {
        return studentsService.add(student);
    }

    @PutMapping
    @Operation(summary = "Update a student", description = "Updates an existing student's details.")
    public Student update(@RequestBody Student student) {
        return studentsService.update(student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Deletes a student by their unique identifier.")
    public void delete(@PathVariable UUID id) {
        studentsService.delete(id);
    }

}
