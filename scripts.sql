--1. Получить всех студентов, возраст которых находится между 10 и 20 (можно подставить любые числа, главное, чтобы нижняя граница была меньше верхней).
--2. Получить всех студентов, но отобразить только список их имен.
--3. Получить всех студентов, у которых в имени присутствует буква «О» (или любая другая).
--4. Получить всех студентов, у которых возраст меньше идентификатора.
--5. Получить всех студентов упорядоченных по возрасту.

SELECT * FROM students WHERE age BETWEEN 10 AND 20;
SELECT name FROM students;
SELECT * FROM students WHERE name LIKE '%o%';
SELECT * FROM students WHERE age < id;
SELECT * FROM students ORDER BY age;

select * from faculty;
SELECT count(*) AS count FROM student;
SELECT AVG(age) AS avg FROM student;
SELECT * FROM student ORDER BY id DESC LIMIT 5;
