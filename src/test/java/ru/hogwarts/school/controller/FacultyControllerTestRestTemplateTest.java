package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;
    @LocalServerPort
    private int port;
    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void clear() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void contextLoads() throws Exception {
        assertThat(facultyController).isNotNull();
    }

    @Test
    public void createFacultyTest() {

        Faculty faculty = createFaculty();

        ResponseEntity<Faculty> forEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Faculty created = forEntity.getBody();
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculty);
        Optional<Faculty> fromDb = facultyRepository.findById(created.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculty);

    }

    @Test
    public void updateFacultyTest() {

        Faculty faculty = createFaculty();
        System.out.println(faculty);
        ResponseEntity<Faculty> forEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/faculty", faculty, Faculty.class);
        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Faculty created = forEntity.getBody();
        Optional<Faculty> fromDb = facultyRepository.findById(created.getId());
        System.out.println(fromDb);
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculty);
        Faculty newFaculty = new Faculty();
        newFaculty.setName(faker.harryPotter().house());
        newFaculty.setColor(faker.color().name());
        long id = created.getId();
        System.out.println(newFaculty);
        System.out.println(id);

        testRestTemplate.put("http://localhost:" + port + "/faculty/" + id, newFaculty);

        Optional<Faculty> fromDbNew = facultyRepository.findById(id);
        System.out.println(fromDbNew);
        assertThat(fromDbNew).isPresent();
        assertThat(fromDbNew.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newFaculty);
    }


    @Test
    public void deleteFaculty() {

        Faculty faculty = createFaculty();
        facultyRepository.save(faculty);
        long id = 0;
        Collection<Faculty> collection = facultyRepository.findAll();
        for (Faculty element : collection) {
            if (element != null) {
                if (element.getName().equals(faculty.getName())) {
                    id = element.getId();
                }
            }
        }
        assertThat(facultyRepository.findById(id)).isPresent();
        Assertions.assertEquals(facultyRepository.findById(id).get(), faculty);

        testRestTemplate.delete("http://localhost:" + port + "/faculty/" + id);

        assertThat(facultyRepository.findById(id)).isEmpty();
    }

    @Test
    public void getFaculty() {

        Faculty faculty = createFaculty();
        facultyRepository.save(faculty);
        long id = 0;
        Collection<Faculty> collection = facultyRepository.findAll();
        for (Faculty element : collection) {
            if (element != null) {
                if (element.getName().equals(faculty.getName())) {
                    id = element.getId();
                }
            }
        }
        assertThat(facultyRepository.findById(id)).isPresent();
        Assertions.assertEquals(facultyRepository.findById(id).get(), faculty);
        assertThat(facultyRepository.findById(id)).isPresent();
        Assertions.assertEquals(facultyRepository.findById(id).get(), faculty);

        ResponseEntity<Faculty> facultyNew = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/" + id, Faculty.class);

        Assertions.assertEquals(facultyNew.getBody(), faculty);
    }

    @Test
    public void getFacultyNegative() {

        List<Faculty> facultyList = facultyRepository.findAll();
        assertThat(facultyList).isEmpty();

        long id = 0;
        ResponseEntity<String> facultyNew = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/" + id, String.class);

        assertThat(facultyNew.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(facultyNew.getBody()).isEqualTo("Факультет с id = %d не найден!".formatted(id));
    }

    @Test
    public void findAllByColor() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();
        Faculty faculty3 = createFaculty();
        Faculty faculty4 = createFaculty();
        Faculty faculty5 = createFaculty();
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(faculty1);
        faculties.add(faculty2);
        faculties.add(faculty3);
        faculties.add(faculty4);
        faculties.add(faculty5);
        facultyRepository.save(faculty1);
        facultyRepository.save(faculty2);
        facultyRepository.save(faculty3);
        facultyRepository.save(faculty4);
        facultyRepository.save(faculty5);
        Assertions.assertEquals(facultyRepository.count(), 5);
        List<Faculty> facultiesNew = new ArrayList<>();
        facultiesNew = facultyRepository.findAll();
        assertThat(facultiesNew)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculties);
        int countNew = 0;
        for (Faculty element : faculties) {
            if (element.getColor() != null) {
                if (element.getColor().equals(faculty2.getColor())) {
                    countNew++;
                }
            }
        }
        System.out.println(countNew);

        String color = faculty2.getColor();
        System.out.println(color);
        ResponseEntity<Collection<Faculty>> facultyCollection =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty?color=" + color,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });
        System.out.println(facultyCollection);
        int count = 0;
        Collection<Faculty> facultyListNew = facultyCollection.getBody();
        System.out.println(facultyListNew);
        for (Faculty element : facultyListNew) {
            if (element.getColor() != null) {
                if (element.getColor().equals(faculty2.getColor())) {
                    count++;
                }
            }
        }
        System.out.println(count);

        Assertions.assertEquals(facultyCollection.getStatusCode(), HttpStatusCode.valueOf(200));
        Assertions.assertEquals(count, countNew);
    }





    @Test
    public void findStudentsByFacultyId() {

        Faculty faculty1 = createFaculty();
        facultyRepository.save(faculty1);

        Student student1 = createStudent(faculty1);
        Student student2 = createStudent(faculty1);
        Student student3 = createStudent(faculty1);
        Student student4 = createStudent(faculty1);
        Student student5 = createStudent(faculty1);
        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);
        studentList.add(student4);
        studentList.add(student5);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        studentRepository.save(student4);
        studentRepository.save(student5);

        Assertions.assertEquals(studentRepository.count(), 5);
        Assertions.assertEquals(1, facultyRepository.count());


        long id = student1.getFaculty().getId();
        ResponseEntity<List<Student>> studentCollection =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + id + "/students",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });
        List<Student> students = studentCollection.getBody();
        System.out.println(students);

        Assertions.assertEquals(studentCollection.getStatusCode(), HttpStatusCode.valueOf(200));
        assertThat(students).isEqualTo(studentList);
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return faculty;
    }

    private Student createStudent(Faculty faculty) {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setFaculty(faculty);
        student.setAge(faker.random().nextInt(10, 20));
        return student;
    }
}
