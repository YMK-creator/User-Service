package com.example.demo.service;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponseDto> getAllUsers(int page, int size);

    UserResponseDto getUserById(Long id);

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);

    void activateUser(Long id, boolean active);
}

