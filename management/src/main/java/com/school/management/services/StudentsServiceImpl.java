package com.school.management.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.school.management.constants.Constants;
import com.school.management.dto.CourseDTO;
import com.school.management.dto.PageResult;
import com.school.management.dto.StudentDTO;
import com.school.management.models.Student;
import com.school.management.repositories.StudentsRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentsServiceImpl implements StudentsService {

    private final StudentsRepository studentsRepository;

    @Override
    public PageResult<StudentDTO> getAll(Student probe, Integer pageSize, Integer pageNumber) {
        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (probe == null) {
            probe = new Student();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Example<Student> example = Example.of(probe);
        Page<Student> page = studentsRepository.findAll(example, pageable);
        List<StudentDTO> dtos = page.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageResult<>(dtos, page.getTotalElements());
    }

    @Override
    public Student add(Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public Student update(Student student) {

        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        if (!studentsRepository.existsById(student.getId())) {
            throw new EntityNotFoundException();
        }

        return studentsRepository.save(student);
    }

    @Override
    public void delete(UUID id) {

        if (!studentsRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        studentsRepository.deleteById(id);
    }

    public StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setAge(student.getAge());
        dto.setGroup(student.getGroup());

        List<CourseDTO> courseDTOs = student.getCourses().stream().map(course -> {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setId(course.getId());
            courseDTO.setName(course.getName());
            courseDTO.setCourseType(course.getCourseType());
            return courseDTO;
        }).toList();

        dto.setCourses(courseDTOs);
        return dto;
    }

}
