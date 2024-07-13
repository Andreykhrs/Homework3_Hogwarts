package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long idGenerator = 1;

    public Student create(Student student) {
        student.setId(idGenerator++);
        students.put(student.getId(), student);
        return student;
    }

    public void update(long id, Student student) {
        if(!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }
        student.setId(id);
        students.replace(id, student);

    }

    public Student get(long id) {
        if(!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }
        return students.get(id);
    }

    public Student remove(long id) {
        if(!students.containsKey(id)) {
            throw new StudentNotFoundException(id);
        }
        return students.remove(id);
    }


}
