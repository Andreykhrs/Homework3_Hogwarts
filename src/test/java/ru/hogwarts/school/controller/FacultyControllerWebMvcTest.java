package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.h2.util.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @Test
    public void createPositiveTest() throws Exception {
        long id = 1;
        String newName = "Гриффиндор";
        String newColor = "красный";



        Faculty newFaculty = new Faculty();
        newFaculty.setId(id);
        newFaculty.setName(newName);
        newFaculty.setColor(newColor);


        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/faculty")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(newFaculty))
                ).andExpect(status().isOk());
        verify(facultyRepository, only()).save(any());

    }
    @Test
    public void updatePositiveTest() throws Exception {
        long id = 1;
        String newName = "Гриффиндор";
        String newColor = "красный";

        Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName("Слизерин");
        oldFaculty.setColor("зеленый");

        Faculty newFaculty = new Faculty();
        newFaculty.setId(id);
        newFaculty.setName(newName);
        newFaculty.setColor(newColor);

        when(facultyRepository.findById(any())).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(newFaculty))
        ).andExpect(status().isOk())
                .andExpect(result -> {
                   String content = result.getResponse().getContentAsString();
                   Faculty actual = objectMapper.readValue(content, Faculty.class);
                   assertThat(actual.equals(newFaculty)).isTrue();
                        }
                        );
    }

    @Test
    public void updateNegativeTest() throws Exception {
        long id = 1;
        String newName = "Гриффиндор";
        String newColor = "красный";

        Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName("Слизерин");
        oldFaculty.setColor("зеленый");

        Faculty newFaculty = new Faculty();
        newFaculty.setId(id);
        newFaculty.setName(newName);
        newFaculty.setColor(newColor);

        when(facultyRepository.findById(id)).thenThrow(FacultyNotFoundException.class);
        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/faculty/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(newFaculty))
        ).andExpect(result -> assertInstanceOf(FacultyNotFoundException.class, result.getResolvedException())
                );
    }

    @Test
    void getFacultyTest() throws Exception {

        long id = 1;
        String name = "Faculty";
        String color = "green";

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setId(id);
        faculty.setColor(color);


        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.color").value(color));
        verify(facultyRepository, only()).findById(any());

    }

    @Test
    void getFacultyNegative() throws Exception {
        long id = 1;
        when(facultyRepository.findById(id)).thenThrow(FacultyNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id))
                .andExpect(result -> assertInstanceOf(FacultyNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void removeFacultyTest() throws Exception {

        long id = 1L;
        String newName = "Гриффиндор";
        String newColor = "красный";


        Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName("Slytherin");
        oldFaculty.setColor("green");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(oldFaculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.existsById(any())).thenReturn(true);


        mockMvc.perform(MockMvcRequestBuilders
                .delete("/faculty/{id}", id));
        verify(facultyRepository, times(1)).deleteById(id);
    }

    @Test
    void removeFacultyNegativeTest() throws Exception {
        long id = 1;
        when(facultyRepository.existsById(id)).thenThrow(FacultyNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id))
                .andExpect(result -> assertInstanceOf(FacultyNotFoundException.class, result.getResolvedException()));

    }

    @Test
    void filterByColorTest() throws Exception {

        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        String name1 = "faculty1";
        String name2 = "faculty2";
        String name3 = "faculty3";
        String color = "green";

        Faculty faculty1 = new Faculty();
        faculty1.setName(name1);
        faculty1.setId(id1);
        faculty1.setColor(color);

        Faculty faculty2 = new Faculty();
        faculty2.setName(name2);
        faculty2.setId(id2);
        faculty2.setColor(color);

        Faculty faculty3 = new Faculty();
        faculty3.setName(name3);
        faculty3.setId(id3);
        faculty3.setColor(color);

        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColor(any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?color=" + color)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value(color))
                .andExpect(jsonPath("$[1].color").value(color))
                .andExpect(jsonPath("$[2].color").value(color));

        verify(facultyRepository, only()).findAllByColor(any());
    }

    @Test
    void filterByColorOrNameTestColor() throws Exception {

        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        String name1 = "test1";
        String color = "black";
        String colorOrName = "black";

        Faculty faculty1 = new Faculty();
        faculty1.setName(name1);
        faculty1.setId(id1);
        faculty1.setColor(color);

        Faculty faculty2 = new Faculty();
        faculty2.setName(name1);
        faculty2.setId(id2);
        faculty2.setColor(color);

        Faculty faculty3 = new Faculty();
        faculty3.setName(name1);
        faculty3.setId(id3);
        faculty3.setColor(color);

        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?colorOrName=" + colorOrName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(facultyRepository, only()).findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any());

    }

    @Test
    void filterByColorOrNameTestName() throws Exception {

        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        String name1 = "test1";
        String color = "black";
        String colorOrName = "black";

        Faculty faculty1 = new Faculty();
        faculty1.setName(name1);
        faculty1.setId(id1);
        faculty1.setColor(color);

        Faculty faculty2 = new Faculty();
        faculty2.setName(name1);
        faculty2.setId(id2);
        faculty2.setColor(color);

        Faculty faculty3 = new Faculty();
        faculty3.setName(name1);
        faculty3.setId(id3);
        faculty3.setColor(color);

        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?colorOrName=" + colorOrName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(facultyRepository, only()).findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any());

    }


    @Test
    void findStudentsByFacultyIdTest() throws Exception {
        //data
        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("green");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(12);
        student3.setName(faker.harryPotter().character());
        student3.setFaculty(faculty);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);

        when(studentRepository.findAllByFaculty_Id(any(Long.class))).thenReturn(studentList);
        //test, check
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id + "/students"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).findAllByFaculty_Id(id);
    }


}
