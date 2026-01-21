package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    @Query("select u from User u where u.active = true")
    Page<User> findAllActiveUsers(Pageable pageable);

    @Modifying
    @Query("update User u set u.active = :active where u.id = :id")
    void updateActive(
            @Param("id") Long id,
            @Param("active") boolean active
    );
}
