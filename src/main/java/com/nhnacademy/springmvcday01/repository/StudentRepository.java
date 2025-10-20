package com.nhnacademy.springmvcday01.repository;


import com.nhnacademy.springmvcday01.domain.Student;

public interface StudentRepository {
    boolean exists(String id);
    boolean matches(String id, String password);
    Student getStudent(String id);
    Student save(Student student);
}