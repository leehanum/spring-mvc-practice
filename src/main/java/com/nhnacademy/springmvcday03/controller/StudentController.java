package com.nhnacademy.springmvcday03.controller;

import com.nhnacademy.springmvcday03.domain.Student;
import com.nhnacademy.springmvcday03.exception.StudentNotFoundException;
import com.nhnacademy.springmvcday03.exception.ValidationFailedException;
import com.nhnacademy.springmvcday03.repository.StudentRepository;
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

        // LoginCheckInterceptor 적용함. 세션 체크는 LoginCheckInterceptor에서
        Student student = studentRepository.getStudent(studentId);
        ModelAndView studentView = new ModelAndView("studentView");
        studentView.addObject("student", student);
        return studentView;
    }

    @GetMapping("/{studentId}/modify")
    public String studentModifyForm(@PathVariable("studentId") String studentId,
                                    Model model,
                                    HttpServletRequest request){
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
        studentRepository.save(student);
        return "redirect:/student/" + studentId;

    }
}
