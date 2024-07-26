package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByAge(int age);
    List<Student> findAllByBetween(int minAge, int maxAge);
    List<Student> findAllByFaculty_Id(long facultyId);

}
