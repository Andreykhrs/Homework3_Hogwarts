package ru.hogwarts.school.controller;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final AvatarService avatarService;


    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentService.create(student);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Student student) {
        studentService.update(id, student);
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable long id) {
       return studentService.get(id);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        studentService.remove(id);
    }

    @GetMapping(params = "age")
    public List<Student> filterByAge(@RequestParam int age) {
        return studentService.filterByAge(age);
    }

    @GetMapping(params = {"minAge", "maxAge"})
    public List<Student> filterByRangeAge(@RequestParam int minAge, @RequestParam int maxAge) {
        return studentService.filterByRangeAge(minAge, maxAge);

    }

    @GetMapping("/{id}/faculty")
    public Faculty findStudentsFaculty(@PathVariable long id) {
        return studentService.findStudentsFaculty(id);
    }

    @GetMapping("/{id}/avatar-from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable long id) {
        return buildResponseEntity(avatarService.getAvatarFromDb(id));

    }

    @GetMapping("/{id}/avatar-from-fs")
    public ResponseEntity<byte[]> getAvatarFromFs(@PathVariable long id) {
        return buildResponseEntity(avatarService.getAvatarFromFs(id));

    }

    private ResponseEntity<byte[]> buildResponseEntity(Pair<byte[], String> pair) {
        byte[] data = pair.getFirst();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(data);

    }

    @GetMapping("/count")
    public long getCountStudents() {
        return studentService.getCountStudents();
    }

    @GetMapping("/age-avg")
    public double getAvgAgeStudents() {
        return studentService.getAvgAgeStudents();
    }

    @GetMapping("/desc-five")
    public List<Student> getDescFiveStudents() {
        return studentService.getDescFiveStudents();
    }


    @GetMapping("/allStudentWithNameOnLetterA")
    public List<String> getAllStudentWithNameOnLetterA() {
        return studentService.getAllStudentWithNameOnLetterA();
    }
    @GetMapping("/ageMediumAllStudent")
    public double getAgeMediumAllStudent() {
        return studentService.getAgeMediumAllStudent();
    }
    @GetMapping("/numberTypeInt")
    public long getNumberTypeInt() {
        return studentService.getNumberTypeInt();
    }



}
