package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardCreateDto {

    @NotBlank
    private String number;

    @NotBlank
    private String holder;

    @Future
    private LocalDate expirationDate;
}
