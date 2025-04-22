package com.school.management.services;

import java.util.UUID;

import com.school.management.dto.PageResult;
import com.school.management.dto.StudentDTO;
import com.school.management.models.Student;

/**
 * Service interface for managing students in the school management system.
 * Provides CRUD operations and paginated search functionality.
 */
public interface StudentsService {
    
    /**
     * Retrieves a paginated list of students matching the provided example probe.
     *
     * @param probe The example student to filter results.
     * @param pageSize The number of results per page.
     * @param pageNumber The page number to retrieve.
     * @return A page result containing the list of matching student DTOs.
     */
    PageResult<StudentDTO> getAll(Student probe, Integer pageSize, Integer pageNumber);

    /**
     * Adds a new student to the system.
     *
     * @param student The student to add.
     * @return The saved student.
     */
    Student add(Student student);

    /**
     * Updates an existing student.
     *
     * @param student The student to update.
     * @return The updated student.
     */
    Student update(Student student);

    /**
     * Deletes a student by their ID.
     *
     * @param id The ID of the student to delete.
     */
    void delete(UUID id);

}
