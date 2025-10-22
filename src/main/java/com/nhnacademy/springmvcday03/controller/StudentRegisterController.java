package com.nhnacademy.springmvcday03.controller;

import com.nhnacademy.springmvcday03.domain.Student;
import com.nhnacademy.springmvcday03.exception.ValidationFailedException;
import com.nhnacademy.springmvcday03.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
//        return "studentRegister";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute Student student,
                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationFailedException(bindingResult);
        }
        studentRepository.save(student);
        return "redirect:/student/" +student.getId();

    }
}
