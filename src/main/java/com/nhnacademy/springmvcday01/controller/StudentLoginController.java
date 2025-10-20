package com.nhnacademy.springmvcday01.controller;


import com.nhnacademy.springmvcday01.repository.StudentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
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
    public String login(@CookieValue(value = "SESSION", required = false) String sessionId,
                        Model model) {
        if (Objects.nonNull(sessionId) && studentRepository.exists(sessionId)) {

            model.addAttribute("student",studentRepository.getStudent(sessionId));
            return "studentView";
        }
        return "loginForm";
    }

    @PostMapping("/login") // ModelMap 사용
    public String doLogin(@RequestParam(name = "id") String id,
                          @RequestParam(name = "pwd") String pwd,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          ModelMap modelMap){

        if(studentRepository.matches(id,pwd)){
            HttpSession session = request.getSession(true);

            Cookie cookie = new Cookie("SESSION", session.getId());
            response.addCookie(cookie);

            modelMap.put("id",session.getId());
            return "redirect:/student/" + id;
        } else{
            return "redirect:/login";
        }


    }
}
