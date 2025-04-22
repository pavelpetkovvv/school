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
import com.school.management.dto.TeacherDTO;
import com.school.management.models.Teacher;
import com.school.management.repositories.TeachersRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeachersServiceImpl implements TeachersService {

    private final TeachersRepository teachersRepository;

    @Override
    public PageResult<TeacherDTO> getAll(Teacher probe, Integer pageSize, Integer pageNumber) {
        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (probe == null) {
            probe = new Teacher();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Example<Teacher> example = Example.of(probe);
        Page<Teacher> page = teachersRepository.findAll(example, pageable);
        List<TeacherDTO> dtos = page.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageResult<>(dtos, page.getTotalElements());
    }

    @Override
    public Teacher add(Teacher teacher) {
        return teachersRepository.save(teacher);
    }

    @Override
    public Teacher update(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }

        if (!teachersRepository.existsById(teacher.getId())) {
            throw new EntityNotFoundException();
        }

        return teachersRepository.save(teacher);
    }

    @Override
    public void delete(UUID id) {
        if (!teachersRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        teachersRepository.deleteById(id);
    }

    public TeacherDTO toDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        dto.setAge(teacher.getAge());
        dto.setGroup(teacher.getGroup());

        List<CourseDTO> courseDTOs = teacher.getCourses().stream().map(course -> {
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
