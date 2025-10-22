package com.nhnacademy.springmvcday03.domain;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
    private String id;
    private String password;

    @NotBlank
    private String name;

    @Email
    private String email;

    @Min(0)
    @Max(100)
    private int score;

    @NotBlank
    @Size(min = 0, max = 200)
    private String comment;

    public Student() {
    }

    public Student(String id, String password, String name, String email, int score, String comment) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.score = score;
        this.comment = comment;
    }

    public String getMaskedPassword(){
        return "****";
    }


}
