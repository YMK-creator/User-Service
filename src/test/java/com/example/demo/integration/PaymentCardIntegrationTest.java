package com.example.demo.integration;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardUpdateDto;
import com.example.demo.model.PaymentCard;
import com.example.demo.model.User;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentCardIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository cardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("cardowner@test.com");
        testUser.setName("Owner");
        testUser.setSurname("Tester");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));
        testUser.setActive(true);
        testUser = userRepository.save(testUser);
    }

    @Test
    void createCard_shouldReturnCreatedCard() throws Exception {
        PaymentCardCreateDto dto = new PaymentCardCreateDto();
        dto.setNumber("1234-5678-9012-3456");
        dto.setHolder("JOHN DOE");
        dto.setExpirationDate(LocalDate.of(2030, 12, 31));

        mockMvc.perform(post("/cards/user/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value("1234-5678-9012-3456"))
                .andExpect(jsonPath("$.active").value(true));

        List<PaymentCard> cards = cardRepository.findAllByUserId(testUser.getId());
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getHolder()).isEqualTo("JOHN DOE");
    }

    @Test
    void createCard_shouldFail_whenLimitReached() throws Exception {
        for (int i = 0; i < 5; i++) {
            PaymentCard card = new PaymentCard();
            card.setUser(testUser);
            card.setNumber("0000-0000-0000-000" + i);
            card.setHolder("HOLDER " + i);
            card.setExpirationDate(LocalDate.of(2025, 1, 1));
            card.setActive(true);
            cardRepository.save(card);
        }

        PaymentCardCreateDto dto = new PaymentCardCreateDto();
        dto.setNumber("9999-9999-9999-9999");
        dto.setHolder("NEW HOLDER");
        dto.setExpirationDate(LocalDate.of(2030, 1, 1));

        mockMvc.perform(post("/cards/user/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());

        assertThat(cardRepository.findAllByUserId(testUser.getId())).hasSize(5);
    }

    @Test
    void getCardsByUserId_shouldReturnList() throws Exception {
        PaymentCard card1 = new PaymentCard();
        card1.setUser(testUser);
        card1.setNumber("1111");
        card1.setHolder("Test 1");
        card1.setExpirationDate(LocalDate.now());
        card1.setActive(true);
        cardRepository.save(card1);

        PaymentCard card2 = new PaymentCard();
        card2.setUser(testUser);
        card2.setNumber("2222");
        card2.setHolder("Test 2");
        card2.setExpirationDate(LocalDate.now());
        card2.setActive(true);
        cardRepository.save(card2);

        mockMvc.perform(get("/cards/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].number").value("1111"))
                .andExpect(jsonPath("$[1].number").value("2222"));
    }

    @Test
    void updateCard_shouldUpdateFields() throws Exception {
        PaymentCard card = new PaymentCard();
        card.setUser(testUser);
        card.setNumber("OLD_NUMBER");
        card.setHolder("OLD_HOLDER");
        card.setExpirationDate(LocalDate.now());
        card.setActive(true);
        card = cardRepository.save(card);

        PaymentCardUpdateDto updateDto = new PaymentCardUpdateDto();
        updateDto.setNumber("NEW_NUMBER");
        updateDto.setHolder("NEW_HOLDER");

        mockMvc.perform(put("/cards/" + card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("NEW_NUMBER"))
                .andExpect(jsonPath("$.holder").value("NEW_HOLDER"));

        PaymentCard updated = cardRepository.findById(card.getId()).orElseThrow();
        assertThat(updated.getNumber()).isEqualTo("NEW_NUMBER");
        assertThat(updated.getHolder()).isEqualTo("NEW_HOLDER");
    }

    @Test
    void activateAndDeactivateCard_shouldChangeStatus() throws Exception {
        PaymentCard card = new PaymentCard();
        card.setUser(testUser);
        card.setNumber("1234");
        card.setHolder("Holder");
        card.setExpirationDate(LocalDate.now());
        card.setActive(false);
        card = cardRepository.save(card);

        mockMvc.perform(patch("/cards/" + card.getId() + "/activate"))
                .andExpect(status().isNoContent()); 

        assertThat(cardRepository.findById(card.getId()).get().getActive()).isTrue();

        mockMvc.perform(patch("/cards/" + card.getId() + "/deactivate"))
                .andExpect(status().isNoContent());

        assertThat(cardRepository.findById(card.getId()).get().getActive()).isFalse();
    }

    @Test
    void deleteCard_shouldRemoveFromDb() throws Exception {
        PaymentCard card = new PaymentCard();
        card.setUser(testUser);
        card.setNumber("DELETE_ME");
        card.setHolder("Delete");
        card.setExpirationDate(LocalDate.now());
        card.setActive(true);
        card = cardRepository.save(card);

        mockMvc.perform(delete("/cards/" + card.getId()))
                .andExpect(status().isNoContent());

        assertThat(cardRepository.findById(card.getId())).isEmpty();
    }
}