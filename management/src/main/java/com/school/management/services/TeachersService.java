package com.school.management.services;

import java.util.UUID;

import com.school.management.dto.PageResult;
import com.school.management.dto.TeacherDTO;
import com.school.management.models.Teacher;

/**
 * Service interface for managing teachers in the school management system.
 * Provides CRUD operations and paginated search functionality.
 */
public interface TeachersService {

    /**
     * Retrieves a paginated list of teachers matching the provided example probe.
     *
     * @param probe The example teacher to filter results.
     * @param pageSize The number of results per page.
     * @param offset The page number to retrieve.
     * @return A page result containing the list of matching teacher DTOs.
     */
    PageResult<TeacherDTO> getAll(Teacher probe, Integer pageSize, Integer offset);

    /**
     * Adds a new teacher to the system.
     *
     * @param teacher The teacher to add.
     * @return The saved teacher.
     */
    Teacher add(Teacher teacher);

    /**
     * Updates an existing teacher.
     *
     * @param teacher The teacher to update.
     * @return The updated teacher.
     */
    Teacher update(Teacher teacher);

    /**
     * Deletes a teacher by their ID.
     *
     * @param id The ID of the teacher to delete.
     */
    void delete(UUID id);

}
