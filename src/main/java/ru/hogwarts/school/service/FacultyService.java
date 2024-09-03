package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }


    public Faculty create(Faculty faculty) {
        logger.info("Was invoked method for \"createFaculty\"");
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty update(long id, Faculty faculty) {
        logger.info("Was invoked method for \"updateFaculty\"");
        Faculty oldFaculty = facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("There is not faculty with id = " + id);
            return new FacultyNotFoundException(id);
        });
        oldFaculty.setName(faculty.getName());
        oldFaculty.setColor(faculty.getColor());
        facultyRepository.save(oldFaculty);
        return faculty;
    }


public Faculty get(long id) {
    logger.info("Was invoked method for \"getFaculty\"");
    return facultyRepository.findById(id).orElseThrow(() -> {
        logger.error("There is not faculty with id = " + id);
        return new FacultyNotFoundException(id);
    });
}

public void remove(long id) {
    logger.info("Was invoked method for \"deleteFaculty\"");
    if (!facultyRepository.existsById(id)) {
        logger.error("There is not faculty with id = " + id);
        throw new FacultyNotFoundException(id);
    }

}

public Collection<Faculty> filterByColor(String color) {
    logger.info("Was invoked method for \"findAllByColor\"");
        return facultyRepository.findAllByColor(color);
}
    public Collection<Faculty> filterByColorOrName(String colorOrName) {
        logger.info("Was invoked method for \"findByNameOrColor\"");
        return facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }


    public List<Student> findsStudentsByFacultyId(long id) {
        logger.info("Was invoked method for \"findStudentsByFacultyId\"");
        return studentRepository.findAllByFaculty_Id(id);
    }

    public String getFacultyWithMaxName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparing(String::length)).orElseThrow();
    }
}
