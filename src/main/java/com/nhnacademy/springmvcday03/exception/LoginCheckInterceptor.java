package com.nhnacademy.springmvcday03.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final String LOGIN_STUDENT_ID = "studentId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 기존 세션이 있으면 가져오고, 없으면 null 반환 (false)
        HttpSession session = request.getSession(false);

        // 세션이 없거나, 세션에 로그인 정보(studentId)가 없으면 falselee
        if (Objects.isNull(session) || Objects.isNull(session.getAttribute(LOGIN_STUDENT_ID))) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
