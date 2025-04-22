package com.school.management.controllers;

import static com.school.management.constants.ApiConstants.TEACHERS_URL;

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
import com.school.management.dto.TeacherDTO;
import com.school.management.models.Teacher;
import com.school.management.services.TeachersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(TEACHERS_URL)
@Tag(name = "Teachers", description = "Operations related to teacher management")
public class TeachersController {

    private final TeachersService teachersService;

    @Operation(summary = "Get all teachers", description = "Retrieves a paginated list of teachers based on optional filter criteria.")
    @GetMapping
    public PageResult<TeacherDTO> getAll(@RequestBody(required = false) Teacher probe,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer pageNumber) {
        return teachersService.getAll(probe, pageSize, pageNumber);
    }

    @Operation(summary = "Add a new teacher", description = "Creates a new teacher in the system.")
    @PostMapping
    public Teacher add(@RequestBody Teacher teacher) {
        return teachersService.add(teacher);
    }

    @Operation(summary = "Update a teacher", description = "Updates an existing teacher's details.")
    @PutMapping
    public Teacher update(@RequestBody Teacher teacher) {
        return teachersService.update(teacher);
    }

    @Operation(summary = "Delete a teacher", description = "Deletes a teacher by their unique identifier.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        teachersService.delete(id);
    }

}
