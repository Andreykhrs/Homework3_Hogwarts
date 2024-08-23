package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private AvatarService avatarService;
    @SpyBean
    private StudentService studentService;
    private final Faker faker = new Faker();

    @Test
    void getStudentAge() throws Exception {

        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());

        Student student2 = new Student();
        student1.setId(2L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());

        when(studentRepository.findAllByAge(10)).thenReturn(Arrays.asList(student1, student2));
        mockMvc.perform(get("/student/age?age=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Найти студентов в диапазоне min/max age")
    void findStudentByAgeBetween() throws Exception {
        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(15);
        student1.setName(faker.harryPotter().character());

        Student student2 = new Student();
        student1.setId(2L);
        student1.setAge(18);
        student1.setName(faker.harryPotter().character());

        when(studentRepository.findAllByAgeBetween(10, 20)).thenReturn(Arrays.asList(student1, student2));
        mockMvc.perform(get("/student/minAge_maxAge?minAge=10&&maxAge=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    void getStudent() throws Exception {
        long id = 1L;
        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(15);
        student1.setName(faker.harryPotter().character());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        mockMvc.perform(get("/student/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student1.getName()))
                .andExpect(jsonPath("$.age").value(student1.getAge()));

    }

    @Test
    void getStudentNegative() throws Exception {
        long id = 1;
        when(studentRepository.findById(id)).thenThrow(StudentNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", id))
                .andExpect(result -> assertInstanceOf(StudentNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void createStudent() throws Exception {
        Student student1 = new Student(null, "Ivan", 30);
        when(studentRepository.save(any())).thenReturn(student1);
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student1.getName()))
                .andExpect(jsonPath("$.age").value(student1.getAge()));
    }

    @Test
    void updateStudent() throws Exception {
        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(id);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student1));

        mockMvc.perform(put("/student/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student2.toString()))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).save(any());

    }

    @Test
    void updateStudentNegative1() throws Exception {
        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(id);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);
        when(studentRepository.findById(id)).thenThrow(StudentNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student2.toString()))
                .andExpect(result -> assertInstanceOf(StudentNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void updateStudentNegative2() throws Exception {
        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(id);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student1));
        when(facultyRepository.findById(id)).thenThrow(FacultyNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student2.toString()))
                .andExpect(result -> assertInstanceOf(FacultyNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void removeStudentTest() throws Exception {

        long id = 1L;
        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());

        when(studentRepository.existsById(any())).thenReturn(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        mockMvc.perform(delete("/student/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).deleteById(any());

    }

    @Test
    void deleteStudentNegative() throws Exception {
        long id = 1;
        when(studentRepository.existsById(id)).thenThrow(StudentNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", id))
                .andExpect(result -> assertInstanceOf(StudentNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void findStudentsFaculty() throws Exception {

        long id = 1L;
        String name = faker.harryPotter().house();
        String color = faker.color().name();
        Faculty faculty = new Faculty();
        faculty.setColor(color);
        faculty.setName(name);
        faculty.setId(id);
        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        when(studentRepository.existsById(any())).thenReturn(true);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student1));

        mockMvc.perform(get("/student/" + id + "/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    void getAllStudentWithNameOnLetterA() throws Exception {

        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(20);
        student1.setName("Oleg");
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(22);
        student2.setName("Anton");
        student2.setFaculty(faculty);

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(23);
        student3.setName("Alex");
        student3.setFaculty(faculty);

        Student student4 = new Student();
        student4.setId(4L);
        student4.setAge(23);
        student4.setName("Artur");
        student4.setFaculty(faculty);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);
        studentList.add(student4);
        System.out.println(studentList);
        List<String> studentsName = new ArrayList<>();
        for (Student s : studentList) {
            studentsName.add(s.getName());
        }
        List<String> studentNew = studentsName.stream()
                .filter(s -> s.startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
        System.out.println(studentNew);

        when(studentRepository.findAll()).thenReturn(studentList);

        mockMvc.perform(get("/student/allStudentWithNameOnLetterA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(studentNew.get(0)))
                .andExpect(jsonPath("$[1]").value(studentNew.get(1)))
                .andExpect(jsonPath("$[2]").value(studentNew.get(2)));

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void ageMediumAllStudent() throws Exception {

        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(20);
        student1.setName("Oleg");
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(22);
        student2.setName("Anton");
        student2.setFaculty(faculty);

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(23);
        student3.setName("Alex");
        student3.setFaculty(faculty);

        Student student4 = new Student();
        student4.setId(4L);
        student4.setAge(28);
        student4.setName("Artur");
        student4.setFaculty(faculty);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);
        studentList.add(student4);

        when(studentRepository.findAll()).thenReturn(studentList);

        mockMvc.perform(get("/student/ageMediumAllStudent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(23.25));

        verify(studentRepository, times(1)).findAll();
    }
}
