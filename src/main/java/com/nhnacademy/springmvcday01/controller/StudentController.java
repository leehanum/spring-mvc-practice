package com.nhnacademy.springmvcday01.controller;

import com.nhnacademy.springmvcday01.domain.Student;
import com.nhnacademy.springmvcday01.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/student")
public class StudentController {
    private StudentRepository studentRepository;

    @Autowired
    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{studentId}") // ModelAndView 사용
    public ModelAndView viewStudent(@PathVariable("studentId") String studentId){
        Student student = studentRepository.getStudent(studentId);
        ModelAndView studentView = new ModelAndView("studentView");
        studentView.addObject("student",student);
        return studentView;
    }

    @GetMapping("/{studentId}/modify") // Model 사용
    public String studentModifyForm(@PathVariable("studentId") String studentId, Model model){
        Student student = studentRepository.getStudent(studentId);
        model.addAttribute("student",student);
        return "studentModify";
    }

    @PostMapping("/{studentId}/modify")
    public String studentModify(@PathVariable("studentId") String studentId,
                                @ModelAttribute("student") Student student){
        studentRepository.save(student);
        return "redirect:/student/" + studentId;

    }
}
