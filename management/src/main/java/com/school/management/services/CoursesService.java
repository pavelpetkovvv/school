package com.school.management.services;

import java.util.List;
import java.util.UUID;

import com.school.management.dto.PageResult;
import com.school.management.models.Course;
import com.school.management.models.Student;

/**
 * Service interface for managing courses in the school management system.
 * Provides operations for course CRUD, student and teacher enrollment.
 */
public interface CoursesService {

    /**
     * Retrieves a paginated list of courses matching the provided example probe.
     *
     * @param probe The example course to filter results.
     * @param size The number of results per page.
     * @param page The page number to retrieve.
     * @return A page result containing the list of matching courses.
     */
    PageResult<Course> getAll(Course probe, Integer size, Integer page);

    /**
     * Adds a new course to the system.
     *
     * @param course The course to add.
     * @return The saved course.
     */
    Course add(Course course);

    /**
     * Updates an existing course.
     *
     * @param course The course to update.
     * @return The updated course.
     */
    Course update(Course course);

    /**
     * Deletes a course by its ID.
     * The course must not have any students or teachers enrolled.
     *
     * @param id The ID of the course to delete.
     */
    void delete(UUID id);
    
    /**
     * Retrieves a list of students enrolled in the given course, optionally filtered by group and minimum age.
     *
     * @param courseId The ID of the course.
     * @param group The group name to filter by (optional).
     * @param minAge The minimum age to filter by (optional).
     * @return A list of matching students.
     */
    List<Student> getStudents(UUID courseId, String group, Integer minAge);

    /**
     * Enrolls a list of students into a course.
     *
     * @param studentIds The list of student IDs to enroll.
     * @param id The ID of the course.
     * @return The updated course with enrolled students.
     */
    Course enrollStudentsInCourse(List<UUID> studentIds, UUID id);

    /**
     * Removes a list of students from a course.
     *
     * @param studentIds The list of student IDs to remove.
     * @param id The ID of the course.
     * @return The updated course after deregistration.
     */
    Course deregisterStudentsFromCourse(List<UUID> studentIds, UUID id);

    /**
     * Assigns teachers to a course.
     *
     * @param teacherIds The list of teacher IDs to assign.
     * @param id The ID of the course.
     * @return The updated course with assigned teachers.
     */
    Course assignTeachersToCourse(List<UUID> teacherIds, UUID id);

    /**
     * Deregisters teachers from a course.
     *
     * @param teacherIds The list of teacher IDs to deregister.
     * @param id The ID of the course.
     * @return The updated course after teacher deregistration.
     */
    Course deregisterTeachersFromCourse(List<UUID> teacherIds, UUID id);

}
