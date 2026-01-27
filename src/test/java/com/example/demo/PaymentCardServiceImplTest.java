package com.example.demo;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardResponseDto;
import com.example.demo.dto.PaymentCardUpdateDto;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.PaymentCard;
import com.example.demo.model.User;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.PaymentCardServiceImpl;
import com.example.demo.utils.PaymentCardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper cardMapper;

    @InjectMocks
    private PaymentCardServiceImpl cardService;

    @Test
    void createCard_shouldCreate_whenUserExistsAndLimitNotReached() {
        Long userId = 1L;
        PaymentCardCreateDto dto = new PaymentCardCreateDto();
        User user = new User();
        user.setId(userId);

        PaymentCard cardEntity = new PaymentCard();
        PaymentCard savedCard = new PaymentCard();
        savedCard.setId(100L);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
        responseDto.setId(100L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(cardMapper.toEntity(dto)).thenReturn(cardEntity);
        when(cardRepository.save(cardEntity)).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(responseDto);

        PaymentCardResponseDto result = cardService.createCard(userId, dto);

        assertThat(result.getId()).isEqualTo(100L);
        verify(cardRepository).save(cardEntity);
        assertThat(cardEntity.getUser()).isEqualTo(user);
        assertThat(cardEntity.getActive()).isTrue();
    }

    @Test
    void createCard_shouldThrowException_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(userId, new PaymentCardCreateDto()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_shouldThrowException_whenLimitReached() {
        Long userId = 1L;
        User user = new User();

        List<PaymentCard> fiveCards = List.of(new PaymentCard(), new PaymentCard(), new PaymentCard(), new PaymentCard(), new PaymentCard());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUserId(userId)).thenReturn(fiveCards);

        assertThatThrownBy(() -> cardService.createCard(userId, new PaymentCardCreateDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User already has 5 payment cards");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void getCardsByUserId_shouldReturnList() {
        Long userId = 1L;
        List<PaymentCard> cards = List.of(new PaymentCard());

        when(cardRepository.findAllByUserId(userId)).thenReturn(cards);
        when(cardMapper.toDto(any())).thenReturn(new PaymentCardResponseDto());

        List<PaymentCardResponseDto> result = cardService.getCardsByUserId(userId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getCardById_shouldReturnCard_whenExists() {
        Long cardId = 50L;
        PaymentCard card = new PaymentCard();
        PaymentCardResponseDto dto = new PaymentCardResponseDto();
        dto.setId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(dto);

        PaymentCardResponseDto result = cardService.getCardById(cardId);

        assertThat(result.getId()).isEqualTo(cardId);
    }

    @Test
    void getCardById_shouldThrow_whenNotFound() {
        Long cardId = 50L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getCardById(cardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Card not found");
    }

    @Test
    void updateCard_shouldUpdateFields() {
        Long cardId = 50L;
        PaymentCardUpdateDto updateDto = new PaymentCardUpdateDto();
        updateDto.setHolder("New Holder");
        updateDto.setNumber("1234");

        PaymentCard existingCard = new PaymentCard();
        existingCard.setHolder("Old Holder");
        existingCard.setNumber("0000");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(existingCard)).thenReturn(existingCard);
        when(cardMapper.toDto(existingCard)).thenReturn(new PaymentCardResponseDto());

        cardService.updateCard(cardId, updateDto);

        assertThat(existingCard.getHolder()).isEqualTo("New Holder");
        assertThat(existingCard.getNumber()).isEqualTo("1234");
        verify(cardRepository).save(existingCard);
    }

    @Test
    void activateCard_shouldChangeStatus() {
        Long cardId = 50L;
        PaymentCard card = new PaymentCard();
        card.setActive(false);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        cardService.activateCard(cardId, true);

        assertThat(card.getActive()).isTrue();
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_shouldCallRepository() {
        Long cardId = 50L;
        cardService.deleteCard(cardId);
        verify(cardRepository).deleteById(cardId);
    }
}