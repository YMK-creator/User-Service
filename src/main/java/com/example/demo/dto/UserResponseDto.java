package com.example.demo.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;

    private List<PaymentCardResponseDto> cards;
}

