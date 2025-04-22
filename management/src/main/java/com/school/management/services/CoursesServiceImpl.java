package com.school.management.services;

import static com.school.management.constants.ErrorMessageTemplate.CANNOT_DELETE_COURSE_WITH_STUDENTS;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.school.management.constants.Constants;
import com.school.management.dto.PageResult;
import com.school.management.models.Course;
import com.school.management.models.Student;
import com.school.management.models.Teacher;
import com.school.management.repositories.CoursesRepository;
import com.school.management.repositories.StudentSpecifications;
import com.school.management.repositories.StudentsRepository;
import com.school.management.repositories.TeacherSpecifications;
import com.school.management.repositories.TeachersRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoursesServiceImpl implements CoursesService {

    private final CoursesRepository coursesRepository;

    private final StudentsRepository studentsRepository;

    private final TeachersRepository teachersRepository;

    @Override
    public PageResult<Course> getAll(Course probe, Integer pageSize, Integer pageNumber) {

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (probe == null) {
            probe = new Course();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Example<Course> example = Example.of(probe);
        Page<Course> page = coursesRepository.findAll(example, pageable);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public List<Student> getStudents(UUID courseId, String group, Integer minAge) {
        Specification<Student> spec = (root, query, criteriaBuilder) -> StudentSpecifications.hasCourseId(courseId)
                .toPredicate(root, query, criteriaBuilder);

        if (group != null) {
            spec = spec.and(StudentSpecifications.hasGroup(group));
        }

        if (minAge != null) {
            spec = spec.and(StudentSpecifications.isOlderThan(minAge));
        }

        return studentsRepository.findAll(spec);
    }

    @Override
    public List<Teacher> getTeachers(UUID courseId, String group, Integer minAge) {
        Specification<Teacher> spec = (root, query, criteriaBuilder) -> TeacherSpecifications.hasCourseId(courseId)
                .toPredicate(root, query, criteriaBuilder);

        if (group != null) {
            spec = spec.and(TeacherSpecifications.hasGroup(group));
        }

        if (minAge != null) {
            spec = spec.and(TeacherSpecifications.isOlderThan(minAge));
        }

        return teachersRepository.findAll(spec);
    }

    @Override
    public Course add(Course course) {
        return coursesRepository.save(course);
    }

    @Override
    @Transactional
    public Course enrollStudentsInCourse(List<UUID> studentIds, UUID courseId) {
        Course course = coursesRepository.findById(courseId)
                .orElseThrow(EntityNotFoundException::new);

        List<Student> students = studentsRepository.findAllById(studentIds);

        course.getStudents().addAll(students);

        for (Student student : students) {
            student.getCourses().add(course);
        }

        studentsRepository.saveAll(students);
        return course;
    }

    @Override
    @Transactional
    public Course assignTeachersToCourse(List<UUID> teacherIds, UUID courseId) {
        Course course = coursesRepository.findById(courseId)
                .orElseThrow(EntityNotFoundException::new);

        List<Teacher> teachers = teachersRepository.findAllById(teacherIds);

        course.getTeachers().addAll(teachers);

        for (Teacher teacher : teachers) {
            teacher.getCourses().add(course);
        }

        teachersRepository.saveAll(teachers);
        return course;
    }

    @Override
    public Course deregisterStudentsFromCourse(List<UUID> studentIds, UUID id) {
        Course course = coursesRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        List<Student> studentsToRemove = studentsRepository.findAllById(studentIds);

        course.getStudents().removeAll(studentsToRemove);

        for (Student student : studentsToRemove) {
            student.getCourses().remove(course);
        }

        studentsRepository.saveAll(studentsToRemove);
        return course;
    }

    @Override
    public Course deregisterTeachersFromCourse(List<UUID> teacherIds, UUID id) {
        Course course = coursesRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        List<Teacher> teachersToRemove = teachersRepository.findAllById(teacherIds);

        course.getTeachers().removeAll(teachersToRemove);

        for (Teacher teacher : teachersToRemove) {
            teacher.getCourses().remove(course);
        }

        teachersRepository.saveAll(teachersToRemove);
        return course;
    }

    @Override
    public Course update(Course course) {
        if (course.getId() == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        if (!coursesRepository.existsById(course.getId())) {
            throw new EntityNotFoundException();
        }

        return coursesRepository.save(course);
    }

    @Override
    public void delete(UUID id) {
        Course course = coursesRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (!course.getStudents().isEmpty()) {
            throw new IllegalStateException(CANNOT_DELETE_COURSE_WITH_STUDENTS.getMessage());
        }
        if (!course.getTeachers().isEmpty()) {
            throw new IllegalStateException(CANNOT_DELETE_COURSE_WITH_STUDENTS.getMessage());
        }
        coursesRepository.deleteById(id);
    }

}
