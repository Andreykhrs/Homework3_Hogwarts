package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();
    private final List<Student> students = new ArrayList<>(10);

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }



    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return facultyRepository.save(faculty);
    }

    private Student createStudent() {
        Student student = new Student();
        student.setName(faker.name().name());
        student.setAge(faker.random().nextInt(20, 50));
        return student;
    }



    private String buildUrl(String uriStartsWithSlash) {
        return "http://localhost:%d%s".formatted(port, uriStartsWithSlash);
    }

    @Test
    public void createStudentTest() {

        Faculty faculty = createFaculty();
        Student student1 = createStudent();
        student1.setFaculty(faculty);

        ResponseEntity<Student> forEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                student1,
                Student.class);

        System.out.println(forEntity);
        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Student actual = forEntity.getBody();
        long id = actual.getId();
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student1);
        Student studentInBase = studentRepository.findById(actual.getId()).orElseThrow();
        assertThat(studentInBase)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student1);

    }

    @Test
    public void createStudentNegativeTest() {

        Faculty faculty = new Faculty();
        faculty.setId(-1L);
        Student student1 = createStudent();
        student1.setFaculty(faculty);

        ResponseEntity<String> forEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                student1,
                String.class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(forEntity.getBody()).isEqualTo("Факультет с id = %d не найден!".formatted(-1));
    }

    @Test
    public void createStudentWithOutFacultyWhichNotExistNegative() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(11, 18));
        student.setName(faker.harryPotter().character());

        Faculty faculty = new Faculty();
        faculty.setId(-1L);

        student.setFaculty(faculty);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/student"),
                student,
                String.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Факультет с id = %d не найден!".formatted(-1));
    }

    @Test
    public void findByAgeBetweenTest() {
        int minAge = faker.random().nextInt(11, 18);
        int maxAge = faker.random().nextInt(minAge, 18);
        List<Student> expected = students.stream()
                .filter(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .toList();

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                buildUrl("/student?minAge={minAge}&maxAge={maxAge}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("minAge", minAge, "maxAge", maxAge)
        );
        List<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);

    }

    @Test
    public  void findStudentsFaculty() {

        assertThat(studentRepository.findAll()).isEmpty();
        Faculty faculty1 = createFaculty();
        Student student1 = createStudent();
        student1.setFaculty(faculty1);
        Student student2 = studentRepository.save(student1);
        long id = student2.getId();
        Student studentInBase = studentRepository.findById(student2.getId()).orElseThrow();
        Assertions.assertEquals(student1, studentInBase);

        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + id + "/faculty",
                Faculty.class);
        Faculty faculty = facultyResponseEntity.getBody();
        Faculty facultyInBase = studentRepository.findById(id).get().getFaculty();

        Assertions.assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Assertions.assertEquals(faculty1,faculty);
        Assertions.assertEquals(faculty1,facultyInBase);
    }

    @Test
    public void findFacultyNegative() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/student/{id}/faculty"),
                String.class,
                Map.of("id", -1)
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = %d не найден!".formatted(-1));
    }


    @Test
    public void getStudentTest() {

        assertThat(studentRepository.findAll()).isEmpty();
        Faculty faculty = createFaculty();
        Student student1 = createStudent();
        student1.setFaculty(faculty);
        Student student2 = studentRepository.save(student1);
        assertThat(studentRepository.findAll()).isNotEmpty();
        long id = student2.getId();

        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + id,
                Student.class);

        assertThat(responseEntity.getBody()).isEqualTo(student1);
    }

    @Test
    public void getStudentNegativeTest() {

        assertThat(studentRepository.findAll()).isEmpty();
        long id = 0;

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + id,
                String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = %d не найден!".formatted(id));
    }

    @Test
    public void removeStudentTest() {
        //data
        assertThat(studentRepository.findAll()).isEmpty();
        Faculty faculty1 = createFaculty();
        Student student1 = createStudent();
        student1.setFaculty(faculty1);
        Student student2 = studentRepository.save(student1);
        long id = student2.getId();
        Student studentInBase = studentRepository.findById(student2.getId()).orElseThrow();
        Assertions.assertEquals(student1, studentInBase);

        testRestTemplate.delete("http://localhost:" + port + "/student/" + id);

        assertThat(studentRepository.findAll()).isEmpty();
    }


}