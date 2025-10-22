package com.nhnacademy.springmvcday03.repository.impl;

import com.nhnacademy.springmvcday03.domain.Student;
import com.nhnacademy.springmvcday03.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final Map<String, Student> studentMap = new HashMap<>();

    public StudentRepositoryImpl() {
        Student student = new Student("lee","1234","이한음","abcd@naver.com",100,"Good!");
        studentMap.put(student.getId(),student);
    }

    @Override
    public boolean exists(String id) {
        return studentMap.containsKey(id);
    }

    @Override
    public boolean matches(String id, String password) {
        return Optional.ofNullable((getStudent(id)))
                .map(student -> student.getPassword().equals(password))
                .orElse(false);
    }

    @Override
    public Student getStudent(String id) {
        Student student = studentMap.get(id);
        return student;
    }

    @Override
    public Student save(Student student) {
        Student changeStudent = studentMap.put(student.getId(), student);
        return changeStudent;
    }
}
