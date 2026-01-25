package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private int status;
    private Instant timestamp;
}
