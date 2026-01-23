package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Past
    private LocalDate birthday;

    @Email
    @NotBlank
    private String email;
}
