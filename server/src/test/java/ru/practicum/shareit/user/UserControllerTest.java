package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    final UserDto userDto = UserDto.builder()
            .name("name")
            .email("test@mail.ru")
            .build();
    final User user = new User(1L, "test@mail.ru", "name");


    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithCollectionUsersInBody() {
        when(userService.getAll()).thenReturn(List.of(user));

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(user.getId()))
                .andExpect(jsonPath("$.[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$.[0].name").value(user.getName()));

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionUsersInBody() {
        when(userService.getAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnResponseStatusOkWithUserInBody() {
        when(userService.create(any())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        verify(userService).create(any());
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenReturnResponseStatusOkWithUserInBody() {
        User updatedUser = new User(1L, "update@mail.ru", "update");
        when(userService.update(any(), anyLong())).thenReturn(updatedUser);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()));

        verify(userService).update(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void update_whenUncorrectedId_thenThrowNotFoundException() {
        when(userService.update(any(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).update(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void getUserById_whenInvoked_thenReturnResponseStatusOkWithUserInBody() {
        when(userService.getById(anyLong())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        verify(userService).getById(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserById_whenUncorrectedId_thenThrowNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getById(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenInvoked_thenReturnResponseStatusOk() {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", anyLong()))
                .andExpect(status().isOk());

        verify(userService).delete(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenUncorrectedId_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).delete(anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", anyLong()))
                .andExpect(status().isNotFound());

        verify(userService).delete(anyLong());
    }
}
