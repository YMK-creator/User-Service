package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;

    private List<PaymentCardResponseDto> cards;
}

