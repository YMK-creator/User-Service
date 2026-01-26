package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class PaymentCardResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
}
