package com.nhnacademy.springmvcday03.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.springmvcday03.domain.Student;
import com.nhnacademy.springmvcday03.exception.ValidationFailedException;
import com.nhnacademy.springmvcday03.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentRegisterControllerTest {

    @InjectMocks
    private StudentRegisterController studentRegisterController;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private BindingResult bindingResult;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        sampleStudent = new Student("testId", "testPwd", "TestName", "test@test.com", 90, "Good");
    }

    // =========================================================================
    // 1. ModelAttribute 테스트
    // =========================================================================

    @Test
    void testGetStudent_ReturnsNewStudent() {
        // when
        Student student = studentRegisterController.getStudent();

        // then
        // 반환된 객체가 null이 아니고, Student 타입인지 확인
        assertNotNull(student);
        assertTrue(student instanceof Student);
    }

    // =========================================================================
    // 2. registerForm 테스트 (GET /student/register)
    // =========================================================================

    @Test
    void testRegisterForm_NotLoggedIn_ReturnsLoginForm() {
        // given: 세션이 존재하지 않는 상황
        // registerForm의 코드는 request.getSession()을 호출합니다.
        // 이 경우 request.getSession(true)와 같으므로, 세션이 새로 생성됩니다.
        when(request.getSession()).thenReturn(session);
        // 하지만 세션에 "studentId" 속성이 없는 상황을 가정합니다.
        when(session.getAttribute("studentId")).thenReturn(null);

        // when
        String viewName = studentRegisterController.registerForm(request);

        // then
        // 로그인 정보가 없으므로 "loginForm"으로 리턴해야 합니다.
        assertEquals("loginForm", viewName);
    }

    @Test
    void testRegisterForm_AlreadyLoggedIn_ReturnsRegisterForm() {
        // given: 이미 로그인된 상황 (session과 studentId 속성이 존재하고, Repository에 ID가 있음)
        String studentId = "lee";

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("studentId")).thenReturn(studentId);
        when(studentRepository.exists(studentId)).thenReturn(true);

        // when
        String viewName = studentRegisterController.registerForm(request);

        // then
        // 로그인 되어 있으므로 "studentRegister" 폼으로 리턴해야 합니다.
        assertEquals("studentRegister", viewName);
        // studentRepository.exists()가 호출되었는지 검증
        verify(studentRepository, times(1)).exists(studentId);
    }

    // =========================================================================
    // 3. register 테스트 (POST /student/register)
    // =========================================================================

    @Test
    void testRegister_Success_RedirectsToStudentView() {
        // given: 유효성 검사 통과 상황
        when(bindingResult.hasErrors()).thenReturn(false);
        // StudentRepository.save()가 호출되는지 확인하기 위해 stubbing
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        // when
        String viewName = studentRegisterController.register(sampleStudent, bindingResult);

        // then
        // 저장 후 상세 보기 페이지로 리다이렉트 되는지 확인
        assertEquals("redirect:/student/" + sampleStudent.getId(), viewName);
        // studentRepository.save()가 1회 호출되었는지 검증
        verify(studentRepository, times(1)).save(sampleStudent);
    }

    @Test
    void testRegister_ValidationFailed_ThrowsException() {
        // given: 유효성 검사 실패 상황
        when(bindingResult.hasErrors()).thenReturn(true);

        // when & then
        // ValidationFailedException이 발생하는지 확인
        assertThrows(ValidationFailedException.class, () -> {
            studentRegisterController.register(sampleStudent, bindingResult);
        });

        // 예외가 발생했으므로 studentRepository.save()는 호출되지 않았어야 함
        verify(studentRepository, never()).save(any(Student.class));
    }
}