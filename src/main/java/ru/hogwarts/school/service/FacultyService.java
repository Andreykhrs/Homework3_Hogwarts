package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }


    public Faculty create(Faculty faculty) {
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty update(long id, Faculty faculty) {
        Faculty oldFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));
        oldFaculty.setName(faculty.getName());
        oldFaculty.setColor(faculty.getColor());
        facultyRepository.save(oldFaculty);
        return faculty;
    }


public Faculty get(long id) {
    return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
}

public void remove(long id) {
    Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new FacultyNotFoundException(id));
    facultyRepository.deleteById(id);

}

public Collection<Faculty> filterByColor(String color) {
        return facultyRepository.findAllByColor(color);
}
    public Collection<Faculty> filterByColorOrName(String colorOrName) {
        return facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }


    public List<Student> findsStudentsByFacultyId(long id) {
        return studentRepository.findAllByFaculty_Id(id);
    }
}
