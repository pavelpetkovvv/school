package com.school.tracking;

import static com.school.management.constants.ApiConstants.TEACHERS_URL;
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
import com.school.management.models.Teacher;
import com.school.management.repositories.TeachersRepository;

@SpringBootTest(classes = com.school.management.TrackingApplication.class)
@AutoConfigureMockMvc
public class TeachersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeachersRepository teacherRepository;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
    }

    @Test
    void shouldAddTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("John Doe");
        teacher.setAge(30);
        teacher.setGroup("Group A");

        mockMvc.perform(post(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.group").value("Group A"));
    }

    @Test
    void shouldGetTeachers() throws Exception {
        // Add two teachers
        Teacher teacher1 = new Teacher();
        teacher1.setName("John Doe");
        teacher1.setAge(30);
        teacher1.setGroup("Group A");

        Teacher teacher2 = new Teacher();
        teacher2.setName("Jane Smith");
        teacher2.setAge(32);
        teacher2.setGroup("Group B");

        // Add the teachers to the database
        mockMvc.perform(post(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher1)))
                .andExpect(status().isOk());

        mockMvc.perform(post(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher2)))
                .andExpect(status().isOk());

        // Create a probe object that matches one of the teachers
        Teacher probe = new Teacher();
        probe.setName("John Doe");

        // Get teachers with the probe object
        mockMvc.perform(get(TEACHERS_URL)
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .content(objectMapper.writeValueAsString(probe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("John Doe"));
    }

    @Test
    void shouldUpdateTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("John Doe");

        String response = mockMvc.perform(post(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Teacher created = objectMapper.readValue(response, Teacher.class);
        created.setName("Updated");

        mockMvc.perform(put(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setName("ToDelete");

        String response = mockMvc.perform(post(TEACHERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Teacher created = objectMapper.readValue(response, Teacher.class);

        mockMvc.perform(delete(TEACHERS_URL + "/" + created.getId()))
                .andExpect(status().isOk());

        // Use a probe with the ID to check that the teacher is deleted
        mockMvc.perform(get(TEACHERS_URL)
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
