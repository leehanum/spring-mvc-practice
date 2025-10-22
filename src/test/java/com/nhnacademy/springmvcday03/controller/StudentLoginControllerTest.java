// 경로: spring-mvc-day03/src/test/java/com/nhnacademy/springmvcday03/controller/StudentLoginControllerTest.java
package com.nhnacademy.springmvcday03.controller;

import com.nhnacademy.springmvcday03.domain.Student;
import com.nhnacademy.springmvcday03.repository.StudentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentLoginControllerTest {

    @InjectMocks
    private StudentLoginController studentLoginController;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    private Student sampleStudent;
    private final String TEST_ID = "lee";
    private final String TEST_PWD = "123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleStudent = new Student(TEST_ID, TEST_PWD, "이한음", "lee@test.com", 100, "Good!");
    }

    // =========================================================================
    // 1. login 테스트 (GET /login)
    // =========================================================================

    @Test
    void testLogin_NotLoggedIn_NoSession() {
        // given: 기존 세션이 없는 경우 (getSession(false) -> null)
        when(request.getSession(false)).thenReturn(null);

        // when
        String viewName = studentLoginController.login(request, model);

        // then
        assertEquals("loginForm", viewName);
        verify(model, never()).addAttribute(anyString(), any());
        verify(studentRepository, never()).exists(anyString());
    }

    @Test
    void testLogin_NotLoggedIn_NoStudentIdInSession() {
        // given: 기존 세션은 있으나 studentId 속성이 없는 경우
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("studentId")).thenReturn(null);

        // when
        String viewName = studentLoginController.login(request, model);

        // then
        assertEquals("loginForm", viewName);
        verify(model, never()).addAttribute(anyString(), any());
        verify(studentRepository, never()).exists(anyString());
    }

    @Test
    void testLogin_AlreadyLoggedIn_IdExists() {
        // given: 로그인 성공 상태 (세션 O, studentId O, ID 존재 O)
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("studentId")).thenReturn(TEST_ID);
        when(studentRepository.exists(TEST_ID)).thenReturn(true);
        when(studentRepository.getStudent(TEST_ID)).thenReturn(sampleStudent);

        // when
        String viewName = studentLoginController.login(request, model);

        // then
        assertEquals("studentView", viewName);
        verify(model, times(1)).addAttribute("student", sampleStudent);
        verify(studentRepository, times(1)).exists(TEST_ID);
        verify(studentRepository, times(1)).getStudent(TEST_ID);
    }

    @Test
    void testLogin_AlreadyLoggedIn_IdNotExists() {
        // given: 세션은 있으나 ID가 Repository에 없는 경우 (데이터 불일치)
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("studentId")).thenReturn(TEST_ID);
        when(studentRepository.exists(TEST_ID)).thenReturn(false);

        // when
        String viewName = studentLoginController.login(request, model);

        // then
        assertEquals("loginForm", viewName);
        verify(model, never()).addAttribute(anyString(), any());
        verify(studentRepository, times(1)).exists(TEST_ID);
        verify(studentRepository, never()).getStudent(anyString());
    }

    // =========================================================================
    // 2. doLogin 테스트 (POST /login)
    // =========================================================================

    @Test
    void testDoLogin_Success() {
        // given: ID/PWD 매칭 성공
        when(studentRepository.matches(TEST_ID, TEST_PWD)).thenReturn(true);
        when(request.getSession(true)).thenReturn(session); // 세션 생성 모킹

        // when
        String viewName = studentLoginController.doLogin(TEST_ID, TEST_PWD, request, response);

        // then
        assertEquals("redirect:/student/" + TEST_ID, viewName);
        verify(studentRepository, times(1)).matches(TEST_ID, TEST_PWD);
        verify(request, times(1)).getSession(true); // 세션 생성 확인
        verify(session, times(1)).setAttribute("studentId", TEST_ID); // 세션에 ID 저장 확인
        // 쿠키 추가 로직 검증 (addCookie가 1회 호출되었는지 확인)
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testDoLogin_Failure() {
        // given: ID/PWD 매칭 실패
        when(studentRepository.matches(TEST_ID, TEST_PWD)).thenReturn(false);

        // when
        String viewName = studentLoginController.doLogin(TEST_ID, TEST_PWD, request, response);

        // then
        assertEquals("redirect:/login", viewName);
        verify(studentRepository, times(1)).matches(TEST_ID, TEST_PWD);
        verify(request, never()).getSession(true); // 세션 생성 시도 없어야 함
        verify(response, never()).addCookie(any(Cookie.class));
    }

    // =========================================================================
    // 3. doLogout 테스트 (GET /logout)
    // =========================================================================

    @Test
    void testDoLogout_Success() {
        // given: 기존 세션이 있는 경우
        when(request.getSession(false)).thenReturn(session);

        // when
        String viewName = studentLoginController.doLogout(request, response);

        // then
        assertEquals("redirect:/login", viewName);
        verify(session, times(1)).invalidate(); // 세션 무효화 확인

        // 쿠키 제거 로직 검증 (setMaxAge(0)과 setPath("/")가 포함된 쿠키가 추가되었는지 확인)
        verify(response, times(1)).addCookie(argThat(cookie ->
                "SESSION".equals(cookie.getName()) &&
                        cookie.getMaxAge() == 0 &&
                        "/".equals(cookie.getPath())
        ));
    }
}