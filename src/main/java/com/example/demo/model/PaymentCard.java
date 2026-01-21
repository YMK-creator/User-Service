package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards", indexes = {@Index(name = "idx_payment_cards_user_id", columnList = "user_id")})
@Getter
@Setter
@NoArgsConstructor
public class PaymentCard extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String number;
    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private Boolean active;
}
