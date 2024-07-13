package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacultyService {

    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long idGenerator = 1;

    public Faculty create(Faculty faculty) {
        faculty.setId(idGenerator++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public void update(long id, Faculty faculty) {
        if(!faculties.containsKey(id)) {
            throw new FacultyNotFoundException(id);
        }
        faculty.setId(id);
        faculties.replace(id, faculty);
       // Faculty oldFaculty = faculties.get(id);
       // oldFaculty.setName(faculty.getName());
       // oldFaculty.setColor(faculty.getColor());
    }

    public Faculty get(long id) {
        if(!faculties.containsKey(id)) {
            throw new FacultyNotFoundException(id);
        }
        return faculties.get(id);
    }

    public Faculty remove(long id) {
        if(!faculties.containsKey(id)) {
            throw new FacultyNotFoundException(id);
        }
        return faculties.remove(id);
    }




}
