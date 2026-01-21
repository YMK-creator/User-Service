package com.example.demo.specification;

import com.example.demo.model.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> hasName(String name) {
        return (root, query, cb) ->
                name == null
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, cb) ->
                surname == null
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    }
}
