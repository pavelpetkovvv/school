package com.school.tracking;

import static com.school.management.constants.ApiConstants.COURSES_URL;
import static com.school.management.constants.ApiConstants.STUDENTS_URL;
import static com.school.management.constants.ApiConstants.TEACHERS_URL;
import static com.school.management.constants.ErrorMessageTemplate.CANNOT_DELETE_COURSE_WITH_STUDENTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.management.models.Course;
import com.school.management.models.CourseType;
import com.school.management.models.Student;
import com.school.management.models.Teacher;
import com.school.management.repositories.CoursesRepository;
import com.school.management.repositories.StudentsRepository;
import com.school.management.repositories.TeachersRepository;

@SpringBootTest(classes = com.school.management.TrackingApplication.class)
@AutoConfigureMockMvc
public class CoursesControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private CoursesRepository coursesRepository;

        @Autowired
        private StudentsRepository studentsRepository;

        @Autowired
        private TeachersRepository teachersRepository;

        @BeforeEach
        void setUp() {
                studentsRepository.deleteAll();
                teachersRepository.deleteAll();
                coursesRepository.deleteAll();
        }

        @Test
        void shouldAddCourse() throws Exception {
                Course course = new Course();
                course.setName("Math 101");
                course.setCourseType(CourseType.MAIN);

                mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Math 101"))
                                .andExpect(jsonPath("$.courseType").value("MAIN"));
        }

        @Test
        void shouldGetCourses() throws Exception {
                // Add two courses
                Course course1 = new Course();
                course1.setName("Math 101");
                course1.setCourseType(CourseType.MAIN);

                Course course2 = new Course();
                course2.setName("Physics 101");
                course2.setCourseType(CourseType.SECONDARY);

                // Add the courses to the database
                mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course1)))
                                .andExpect(status().isOk());

                mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course2)))
                                .andExpect(status().isOk());

                // Create a probe object that matches one of the courses
                Course probe = new Course();
                probe.setName("Math 101");

                // Get courses with the probe object
                mockMvc.perform(get(COURSES_URL)
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                                .content(objectMapper.writeValueAsString(probe)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items[0].name").value("Math 101"));
        }

        @Test
        void shouldUpdateCourse() throws Exception {
                Course course = new Course();
                course.setName("Math 101");

                String response = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course created = objectMapper.readValue(response, Course.class);
                created.setName("Math 102");

                mockMvc.perform(put(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(created)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Math 102"));
        }

        @Test
        void shouldDeleteCourse() throws Exception {
                Course course = new Course();
                course.setName("ToDelete");

                String response = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course created = objectMapper.readValue(response, Course.class);

                mockMvc.perform(delete(COURSES_URL + "/" + created.getId()))
                                .andExpect(status().isOk());

                // Use a probe with the ID to check that the course is deleted
                mockMvc.perform(get(COURSES_URL)
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                                .content(objectMapper.writeValueAsString(created)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items").isEmpty());
        }

        @Test
        void shouldNotDeleteCourseWithStudents() throws Exception {
                // Add a course
                Course course = new Course();
                course.setName("Math 101");
                course.setCourseType(CourseType.MAIN);

                String courseResponse = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course createdCourse = objectMapper.readValue(courseResponse, Course.class);

                // Add a student
                Student student = new Student();
                student.setName("John Doe");

                String studentResponse = mockMvc.perform(post(STUDENTS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Student createdStudent = objectMapper.readValue(studentResponse, Student.class);

                // Enroll the student in the course
                mockMvc.perform(post(COURSES_URL + "/" + createdCourse.getId() + "/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(List.of(createdStudent.getId()))))
                                .andExpect(status().isOk());

                // Try to delete the course while the student is enrolled
                mockMvc.perform(delete(COURSES_URL + "/" + createdCourse.getId()))
                                .andExpect(status().isBadRequest()) // Expecting Bad Request since the course has
                                                                    // enrolled students
                                .andExpect(jsonPath("$.error.message")
                                                .value(CANNOT_DELETE_COURSE_WITH_STUDENTS.getMessage()));
        }

        @Test
        void shouldDeregisterStudentsFromCourse() throws Exception {
                // Add a course
                Course course = new Course();
                course.setName("Math 101");
                course.setCourseType(CourseType.MAIN);

                String courseResponse = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course createdCourse = objectMapper.readValue(courseResponse, Course.class);

                // Add students
                Student student1 = new Student();
                student1.setName("John Doe");
                Student student2 = new Student();
                student2.setName("Jane Doe");

                String studentResponse1 = mockMvc.perform(post(STUDENTS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student1)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Student createdStudent1 = objectMapper.readValue(studentResponse1, Student.class);

                String studentResponse2 = mockMvc.perform(post(STUDENTS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student2)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Student createdStudent2 = objectMapper.readValue(studentResponse2, Student.class);

                // Enroll students in the course
                mockMvc.perform(post(COURSES_URL + "/" + createdCourse.getId() + "/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                List.of(createdStudent1.getId(), createdStudent2.getId()))))
                                .andExpect(status().isOk());

                // Deregister students from the course
                mockMvc.perform(delete(COURSES_URL + "/" + createdCourse.getId() + "/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                List.of(createdStudent1.getId(), createdStudent2.getId()))))
                                .andExpect(status().isOk());

                // Check if the students were deregistered
                mockMvc.perform(get(COURSES_URL + "/" + createdCourse.getId() + "/students"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0)); // Verify that the course has no students
        }

        @Test
        void shouldGetStudentsForCourse() throws Exception {
                // Add a course
                Course course = new Course();
                course.setName("Math 101");
                course.setCourseType(CourseType.MAIN);

                String courseResponse = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course createdCourse = objectMapper.readValue(courseResponse, Course.class);

                // Add a student
                Student student1 = new Student();
                student1.setName("John Doe");
                student1.setAge(20);
                student1.setGroup("Group A");

                Student student2 = new Student();
                student2.setName("John Doe");
                student2.setAge(20);
                student2.setGroup("Group B");

                String studentResponse1 = mockMvc.perform(post(STUDENTS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student1)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Student createdStudent1 = objectMapper.readValue(studentResponse1, Student.class);

                String studentResponse2 = mockMvc.perform(post(STUDENTS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student2)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Student createdStudent2 = objectMapper.readValue(studentResponse2, Student.class);

                // Enroll both students in the course
                mockMvc.perform(post(COURSES_URL + "/" + createdCourse.getId() + "/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                List.of(createdStudent1.getId(), createdStudent2.getId()))))
                                .andExpect(status().isOk());

                // Fetch students for the course
                mockMvc.perform(get(COURSES_URL + "/" + createdCourse.getId() + "/students")
                                .param("group", "Group A") // Optional filters
                                .param("minAge", "18"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1)) // Check that there is only one student
                                .andExpect(jsonPath("$[0].name").value("John Doe"));
        }

        @Test
        void shouldDeregisterTeachersFromCourse() throws Exception {
                // Add a course
                Course course = new Course();
                course.setName("Math 101");
                course.setCourseType(CourseType.MAIN);

                String courseResponse = mockMvc.perform(post(COURSES_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Course createdCourse = objectMapper.readValue(courseResponse, Course.class);

                // Add teachers
                Teacher teacher1 = new Teacher();
                teacher1.setName("John Doe");
                Teacher teacher2 = new Teacher();
                teacher2.setName("Jane Doe");

                String teacherResponse1 = mockMvc.perform(post(TEACHERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(teacher1)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Teacher createdTeacher1 = objectMapper.readValue(teacherResponse1, Teacher.class);

                String teacherResponse2 = mockMvc.perform(post(TEACHERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(teacher2)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Teacher createdTeacher2 = objectMapper.readValue(teacherResponse2, Teacher.class);

                // Enroll teachers in the course
                mockMvc.perform(post(COURSES_URL + "/" + createdCourse.getId() + "/teachers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                List.of(createdTeacher1.getId(), createdTeacher2.getId()))))
                                .andExpect(status().isOk());

                // Deregister teachers from the course
                mockMvc.perform(delete(COURSES_URL + "/" + createdCourse.getId() + "/teachers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                List.of(createdTeacher1.getId(), createdTeacher2.getId()))))
                                .andExpect(status().isOk());

                Course probe = new Course();
                probe.setId(createdCourse.getId());

                // Check if the teachers were deregistered
                mockMvc.perform(get(COURSES_URL)
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                                .content(objectMapper.writeValueAsString(probe)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items[0].teachers.length()").value(0));
        }

}
