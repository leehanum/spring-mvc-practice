package com.nhnacademy.springmvcday02.controller;

import com.nhnacademy.springmvcday02.domain.Student;
import com.nhnacademy.springmvcday02.exception.StudentNotFoundException;
import com.nhnacademy.springmvcday02.exception.ValidationFailedException;
import com.nhnacademy.springmvcday02.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    // 여기서 먼저 StudentNotFoundException을 잡음! 여기서 안잡히면 /advice/WebControllerAdvice에서 잡힘.
    @ExceptionHandler({StudentNotFoundException.class, })
    public String notFound(StudentNotFoundException ex, Model model){
        model.addAttribute("exception", ex);
        return "error";
    }

    @GetMapping("/{studentId}")
    public ModelAndView viewStudent(@PathVariable("studentId") String studentId,
                                    HttpServletRequest request){
        HttpSession session = request.getSession(false);

        // 세션이 없거나, 세션의 학생 ID가 요청한 ID와 다르면
        if (session == null || !studentId.equals(session.getAttribute("studentId"))) {
            throw new StudentNotFoundException("404 error");
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
            throw new StudentNotFoundException("404 error");
        }

        Student student = studentRepository.getStudent(studentId);
        model.addAttribute("student", student);
        return "studentModify";
    }

    @PostMapping("/{studentId}/modify")
    public String studentModify(@PathVariable("studentId") String studentId,
                                @Valid @ModelAttribute("student") Student student,
                                BindingResult bindingResult,
                                HttpServletRequest request){
        if(bindingResult.hasErrors()){
            throw new ValidationFailedException(bindingResult);
        }

        HttpSession session = request.getSession(false);

        if (session == null || !studentId.equals(session.getAttribute("studentId"))) {
            return "redirect:/login";
        }

        studentRepository.save(student);
        return "redirect:/student/" + studentId;
    }
}
