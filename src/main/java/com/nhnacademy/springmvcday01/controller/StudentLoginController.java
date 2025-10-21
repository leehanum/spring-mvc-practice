package com.nhnacademy.springmvcday01.controller;


import com.nhnacademy.springmvcday01.repository.StudentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class StudentLoginController {
    private StudentRepository studentRepository;

    @Autowired
    public StudentLoginController(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 기존 세션만 가져오기

        if (Objects.nonNull(session)) {
            String studentId = (String) session.getAttribute("studentId");

            if (Objects.nonNull(studentId) && studentRepository.exists(studentId)) {
                model.addAttribute("student", studentRepository.getStudent(studentId));
                return "studentView";
            }
        }

        return "loginForm";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam(name = "id") String id,
                          @RequestParam(name = "pwd") String pwd,
                          HttpServletRequest request,
                          HttpServletResponse response){

        if(studentRepository.matches(id, pwd)){
            HttpSession session = request.getSession(true);

            session.setAttribute("studentId", id);

            Cookie cookie = new Cookie("SESSION", session.getId());
            response.addCookie(cookie);

            return "redirect:/student/" + id;
        } else {
            return "redirect:/login";
        }
    }
}
