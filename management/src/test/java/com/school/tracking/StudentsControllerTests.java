package com.school.tracking;

import static com.school.management.constants.ApiConstants.STUDENTS_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.management.models.Student;
import com.school.management.repositories.StudentsRepository;

@SpringBootTest(classes = com.school.management.TrackingApplication.class)
@AutoConfigureMockMvc
public class StudentsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentsRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    void shouldAddStudent() throws Exception {
        Student student = new Student();
        student.setName("John Doe");
        student.setAge(20);
        student.setGroup("Group A");

        mockMvc.perform(post(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.group").value("Group A"));
    }

    @Test
    void shouldGetStudents() throws Exception {
        // Add two students
        Student student1 = new Student();
        student1.setName("John Doe");
        student1.setAge(20);
        student1.setGroup("Group A");

        Student student2 = new Student();
        student2.setName("Jane Smith");
        student2.setAge(22);
        student2.setGroup("Group B");

        // Add the students to the database
        mockMvc.perform(post(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk());

        mockMvc.perform(post(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student2)))
                .andExpect(status().isOk());

        // Create a probe object that matches one of the students
        Student probe = new Student();
        probe.setName("John Doe");

        // Get students with the probe object
        mockMvc.perform(get(STUDENTS_URL)
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .content(objectMapper.writeValueAsString(probe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("John Doe"));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        Student student = new Student();
        student.setName("John Doe");

        String response = mockMvc.perform(post(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Student created = objectMapper.readValue(response, Student.class);
        created.setName("Updated");

        mockMvc.perform(put(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        Student student = new Student();
        student.setName("ToDelete");

        String response = mockMvc.perform(post(STUDENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Student created = objectMapper.readValue(response, Student.class);

        mockMvc.perform(delete(STUDENTS_URL + "/" + created.getId()))
                .andExpect(status().isOk());

        // Use a probe with the ID to check that the student is deleted
        mockMvc.perform(get(STUDENTS_URL)
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
