package com.example.demo.integration;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.model.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Ivan");
        createDto.setSurname("Ivanov");
        createDto.setEmail("ivan@test.com");
        createDto.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("ivan@test.com"))
                .andExpect(jsonPath("$.active").value(true));
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("ivan@test.com");
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setSurname("User");
        user.setEmail("test@test.com");
        user.setBirthday(LocalDate.now());
        user.setActive(true);
        user = userRepository.save(user);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getAllUsers_shouldReturnOnlyActiveUsers() throws Exception {
        User activeUser = new User();
        activeUser.setEmail("active@test.com");
        activeUser.setActive(true);
        userRepository.save(activeUser);

        User inactiveUser = new User();
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setActive(false);
        userRepository.save(inactiveUser);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("active@test.com"));
    }

    @Test
    void updateUser_shouldUpdateFields() throws Exception {
        User user = new User();
        user.setName("OldName");
        user.setEmail("old@test.com");
        user.setActive(true);
        user = userRepository.save(user);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("NewName");

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("NewName");
        assertThat(updatedUser.getEmail()).isEqualTo("old@test.com");
    }

    @Test
    void deactivateUser_shouldChangeActiveStatus() throws Exception {
        User user = new User();
        user.setEmail("active@test.com");
        user.setActive(true);
        user = userRepository.save(user);

        mockMvc.perform(patch("/users/" + user.getId() + "/deactivate"))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isFalse();
    }

    @Test
    void deleteUser_shouldRemoveFromDb() throws Exception {
        User user = new User();
        user.setEmail("delete@test.com");
        userRepository.save(user);

        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}