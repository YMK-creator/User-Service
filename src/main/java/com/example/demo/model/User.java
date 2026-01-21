package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_users_email", columnList = "email")})
@Getter
@Setter
@NoArgsConstructor
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthday;

    @Column(unique = true, nullable = false)
    private String email;

    private Boolean active;

    @OneToMany(mappedBy = "user")
    private List<PaymentCard> paymentCards;
}
