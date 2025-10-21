package com.nhnacademy.springmvcday01.controller;

import com.nhnacademy.springmvcday01.domain.Student;
import com.nhnacademy.springmvcday01.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/{studentId}")
    public ModelAndView viewStudent(@PathVariable("studentId") String studentId,
                                    HttpServletRequest request){
        HttpSession session = request.getSession(false);

        // 세션이 없거나, 세션의 학생 ID가 요청한 ID와 다르면
        if (session == null || !studentId.equals(session.getAttribute("studentId"))) {
            return new ModelAndView("redirect:/login");
        }

        Student student = studentRepository.getStudent(studentId);
        ModelAndView studentView = new ModelAndView("studentView");
        studentView.addObject("student", student);
        return studentView;
    }

    @GetMapping("/{studentId}/modify")
    public String studentModifyForm(@PathVariable("studentId") String studentId,
                                    Model model,
                                    HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session == null || !studentId.equals(session.getAttribute("studentId"))) {
            return "redirect:/login";
        }

        Student student = studentRepository.getStudent(studentId);
        model.addAttribute("student", student);
        return "studentModify";
    }

    @PostMapping("/{studentId}/modify")
    public String studentModify(@PathVariable("studentId") String studentId,
                                @ModelAttribute("student") Student student,
                                HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session == null || !studentId.equals(session.getAttribute("studentId"))) {
            return "redirect:/login";
        }

        studentRepository.save(student);
        return "redirect:/student/" + studentId;
    }
}
