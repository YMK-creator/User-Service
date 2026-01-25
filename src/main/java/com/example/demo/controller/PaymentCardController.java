package com.example.demo.controller;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardResponseDto;
import com.example.demo.dto.PaymentCardUpdateDto;
import com.example.demo.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardResponseDto>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentCardService.getCardsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCardService.getCardById(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentCardResponseDto> createCard(
            @Valid @RequestBody PaymentCardCreateDto dto, @PathVariable Long userId
    ) {
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService.createCard(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardResponseDto);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<PaymentCardResponseDto> updateCard(
            @PathVariable Long cardId, @Valid @RequestBody PaymentCardUpdateDto dto
    ) {
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService.updateCard(cardId, dto);
        return ResponseEntity.ok(paymentCardResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        paymentCardService.activateCard(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCard(@PathVariable Long id) {
        paymentCardService.activateCard(id, false);
        return ResponseEntity.noContent().build();
    }

}
