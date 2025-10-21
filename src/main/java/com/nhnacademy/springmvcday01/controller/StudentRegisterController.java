package com.nhnacademy.springmvcday01.controller;

import com.nhnacademy.springmvcday01.domain.Student;
import com.nhnacademy.springmvcday01.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/student/register")
public class StudentRegisterController {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentRegisterController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // @ModelAttribute 사용
    @ModelAttribute("student")
    public Student getStudent(){
        return new Student();
    }

    @GetMapping
    public String registerForm(HttpServletRequest request){
        HttpSession session = request.getSession();

        if(Objects.nonNull(session)){
            String studentId = (String) session.getAttribute("studentId");


            if (Objects.nonNull(studentId) && studentRepository.exists(studentId)) {
                return "studentRegister";
            }
        }
        return "loginForm";
    }

    @PostMapping
    public String register(@ModelAttribute Student student){
        studentRepository.save(student);
        return "redirect:/student/" +student.getId();

    }
}
