package com.example.demo;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void createUser_shouldSaveUser() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("test@test.com");

        User userEntity = new User();
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setActive(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(10L);

        when(userMapper.toEntity(createDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(createDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(userEntity.getActive()).isTrue(); // Проверяем бизнес-логику установки активности
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_shouldUpdateFields() {
        Long id = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("NewName");

        User existingUser = new User();
        existingUser.setName("OldName");
        existingUser.setSurname("OldSurname");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(new UserResponseDto());

        userService.updateUser(id, updateDto);

        assertThat(existingUser.getName()).isEqualTo("NewName"); // Имя изменилось
        assertThat(existingUser.getSurname()).isEqualTo("OldSurname"); // Фамилия осталась
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_shouldCallDeleteById() {
        userService.deleteUser(5L);

        verify(userRepository).deleteById(5L);
    }

    @Test
    void getAllUsers_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAllActiveUsers(pageable)).thenReturn(page);
        when(userMapper.toDto(any(User.class))).thenReturn(new UserResponseDto());

        Page<UserResponseDto> result = userService.getAllUsers(0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}