package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {

    private String name;
    private String surname;

    @Past
    private LocalDate birthday;

    @Email
    private String email;
}
