package com.nhnacademy.springmvcday02.exception;

public class StudentNotFoundException extends RuntimeException {
  public StudentNotFoundException(String message) {
    super(message);
  }
}
