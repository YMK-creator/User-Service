package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardUpdateDto {

    private String number;
    private String holder;

    @Future
    private LocalDate expirationDate;
}
