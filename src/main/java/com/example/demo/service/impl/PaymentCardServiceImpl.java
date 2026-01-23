package com.example.demo.service.impl;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardResponseDto;
import com.example.demo.utils.PaymentCardMapper;
import com.example.demo.model.PaymentCard;
import com.example.demo.model.User;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PaymentCardService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper cardMapper;

    public PaymentCardServiceImpl(PaymentCardRepository cardRepository,
                                  UserRepository userRepository,
                                  PaymentCardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    @Transactional
    public PaymentCardResponseDto createCard(Long userId, PaymentCardCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long cardCount = cardRepository.findAllByUserId(userId).size();
        if (cardCount >= 5) {
            throw new RuntimeException("User already has 5 payment cards");
        }

        PaymentCard card = cardMapper.toEntity(dto);
        card.setUser(user);
        card.setActive(true);

        PaymentCard saved = cardRepository.save(card);
        return cardMapper.toDto(saved);
    }

    @Override
    public List<PaymentCardResponseDto> getCardsByUserId(Long userId) {
        return cardRepository.findAllByUserId(userId)
                .stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentCardResponseDto getCardById(Long cardId) {
        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    public PaymentCardResponseDto updateCard(Long cardId, PaymentCardCreateDto dto) {
        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (dto.getNumber() != null) card.setNumber(dto.getNumber());
        if (dto.getHolder() != null) card.setHolder(dto.getHolder());
        if (dto.getExpirationDate() != null) card.setExpirationDate(dto.getExpirationDate());

        PaymentCard updated = cardRepository.save(card);
        return cardMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void activateCard(Long cardId, boolean active) {
        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setActive(active);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
