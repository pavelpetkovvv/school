package com.school.management.controllers;

import static com.school.management.constants.ApiConstants.COURSES_URL;

import java.util.List;
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
import com.school.management.models.Course;
import com.school.management.models.Student;
import com.school.management.models.Teacher;
import com.school.management.services.CoursesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(COURSES_URL)
@Tag(name = "Courses", description = "Endpoints for managing courses and their students")
public class CoursesController {

    private final CoursesService coursesService;

    @Operation(summary = "Get all courses", description = "Returns a paginated list of courses filtered by an optional course probe")
    @GetMapping
    public PageResult<Course> getAll(@RequestBody(required = false) Course probe,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize) {
        return coursesService.getAll(probe, pageSize, pageNumber);
    }

    @Operation(summary = "Get students of a course", description = "Retrieves a list of students enrolled in a course, optionally filtered by group and minimum age")
    @GetMapping("/{id}/students")
    public List<Student> getStudents(@PathVariable UUID id, @RequestParam(required = false) String group,
            @RequestParam(required = false) Integer minAge) {
        return coursesService.getStudents(id, group, minAge);
    }

    @Operation(summary = "Get teachers of a course", description = "Retrieves a list of teachers assigned to a course, optionally filtered by group and minimum age")
    @GetMapping("/{id}/teachers")
    public List<Teacher> getTeachers(@PathVariable UUID id, @RequestParam(required = false) String group,
            @RequestParam(required = false) Integer minAge) {
        return coursesService.getTeachers(id, group, minAge);
    }

    @Operation(summary = "Add students to course", description = "Adds a list of students to the course with the given ID")
    @PostMapping("/{id}/students")
    public Course enrollStudentsInCourse(@RequestBody List<UUID> students, @PathVariable UUID id) {
        return coursesService.enrollStudentsInCourse(students, id);
    }

    @Operation(summary = "Remove students from course", description = "Removes a list of students from the course with the given ID")
    @DeleteMapping("/{id}/students")
    public Course deregisterStudentsFromCourse(@RequestBody List<UUID> students, @PathVariable UUID id) {
        return coursesService.deregisterStudentsFromCourse(students, id);
    }

    @Operation(summary = "Add teachers to course", description = "Adds a list of teachers to the course with the given ID")
    @PostMapping("/{id}/teachers")
    public Course enrollTeachersInCourse(@RequestBody List<UUID> teachers, @PathVariable UUID id) {
        return coursesService.assignTeachersToCourse(teachers, id);
    }

    @Operation(summary = "Remove teachers from course", description = "Removes a list of teachers from the course with the given ID")
    @DeleteMapping("/{id}/teachers")
    public Course deregisterTeachersFromCourse(@RequestBody List<UUID> teachers, @PathVariable UUID id) {
        return coursesService.deregisterTeachersFromCourse(teachers, id);
    }

    @Operation(summary = "Add a new course", description = "Creates a new course")
    @PostMapping
    public Course add(@RequestBody Course Course) {
        return coursesService.add(Course);
    }

    @Operation(summary = "Update a course", description = "Updates an existing course")
    @PutMapping
    public Course get(@RequestBody Course Course) {
        return coursesService.update(Course);
    }

    @Operation(summary = "Delete a course", description = "Deletes the course with the specified ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        coursesService.delete(id);
    }
}
