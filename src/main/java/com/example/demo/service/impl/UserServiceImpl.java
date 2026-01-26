package com.example.demo.service.impl;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAllActiveUsers(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Cacheable(value = "userCache", key = "#id")
    public UserResponseDto getUserById(Long id) {
        return userMapper.toDto(
                userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"))
        );
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    @CachePut(value = "userCache", key = "#id")
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getSurname() != null) user.setSurname(dto.getSurname());
        if (dto.getBirthday() != null) user.setBirthday(dto.getBirthday());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "userCache", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateUser(Long id, boolean active) {
        userRepository.updateActive(id, active);
    }
}
