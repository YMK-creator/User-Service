package com.example.demo.repository;

import com.example.demo.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository
        extends JpaRepository<PaymentCard, Long> {

    Optional<PaymentCard> findById(Long id);

    @Query("select c from PaymentCard c where c.user.id = :userId")
    List<PaymentCard> findAllByUserId(@Param("userId") Long userId);

    @Query(
            value = "select * from payment_cards where active = true",
            nativeQuery = true
    )
    List<PaymentCard> findAllActiveCards();

    @Modifying
    @Query("update PaymentCard c set c.active = :active where c.id = :id")
    void updateActive(
            @Param("id") Long id,
            @Param("active") boolean active
    );
}
