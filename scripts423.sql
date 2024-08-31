SELECT student.name, student.age, faculty.name
FROM student
INNER LEFT JOIN faculty ON student.faculty_id = faculty.id
SELECT student.name, student.age
FROM student
INNER JOIN avatars ON avatars.student_id = student.id