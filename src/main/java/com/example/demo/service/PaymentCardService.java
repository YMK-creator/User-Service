package com.example.demo.service;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardResponseDto;
import com.example.demo.dto.PaymentCardUpdateDto;

import java.util.List;

public interface PaymentCardService {

    PaymentCardResponseDto createCard(Long userId, PaymentCardCreateDto dto);

    List<PaymentCardResponseDto> getCardsByUserId(Long userId);

    PaymentCardResponseDto getCardById(Long cardId);

    PaymentCardResponseDto updateCard(Long cardId, PaymentCardUpdateDto dto);

    void activateCard(Long cardId, boolean active);

    void deleteCard(Long cardId);
}
