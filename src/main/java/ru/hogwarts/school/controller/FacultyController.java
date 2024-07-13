package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty create(@RequestBody Faculty faculty) {
        return facultyService.create(faculty);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Faculty faculty) {
        facultyService.update(id, faculty);
    }

    @GetMapping("/{id}")
    public Faculty get(@PathVariable long id) {
        return facultyService.get(id);
    }

    @DeleteMapping("/{id}")
    public Faculty remove(@PathVariable long id) {
        return facultyService.remove(id);
    }
}