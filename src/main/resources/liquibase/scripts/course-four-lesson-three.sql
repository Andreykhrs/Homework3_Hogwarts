-- liquibase formatted sql

-- changeset andreykhrs:1
CREATE INDEX student_name_index ON student (name);
-- changeset andreykhrs:2
CREATE INDEX faculty_nc_index ON faculty (name, color);
