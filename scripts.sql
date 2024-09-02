--1. Получить всех студентов, возраст которых находится между 10 и 20 (можно подставить любые числа, главное, чтобы нижняя граница была меньше верхней).
--2. Получить всех студентов, но отобразить только список их имен.
--3. Получить всех студентов, у которых в имени присутствует буква «О» (или любая другая).
--4. Получить всех студентов, у которых возраст меньше идентификатора.
--5. Получить всех студентов упорядоченных по возрасту.

select * from student;
SELECT * FROM student WHERE age between 10 and 20;
SELECT name FROM student;
SELECT * FROM student WHERE name ilike  '%O%';
SELECT * FROM student WHERE age<id;
select * from student ORDER BY age;
select * from faculty;
SELECT count(*) AS count FROM student;
SELECT AVG(age) AS avg FROM studentMIT 5;
