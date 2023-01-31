package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    final User user = new User(1L, "test@mail.ru", "test");

    @Test
    void getAll_whenInvoked_thenReturnCollectionUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> actualUsers = userService.getAll();

        assertFalse(actualUsers.isEmpty());
        assertEquals(1, actualUsers.size());
        assertEquals(user, actualUsers.get(0));
    }

    @Test
    void getAll_whenInvoked_thenReturnEmptyCollectionUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> actualUsers = userService.getAll();

        assertTrue(actualUsers.isEmpty());
    }

    @Test
    void create_whenInvoked_thenReturnUser() {
        when(userRepository.save(any())).thenReturn(user);

        User actualUser = userService.create(user);

        assertEquals(user, actualUser);
        verify(userRepository).save(any());
    }

    @Test
    void update_whenFindUserByIdEmpty_thenNotFoundExceptionThrown() {
        User newUser = new User(1L, "test@yandex.ru", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.update(newUser, newUser.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void update_whenInvoked_thenReturnUser() {
        User newUser = new User(1L, "test@yandex.ru", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User actualUser = userService.update(newUser, newUser.getId());

        assertEquals(newUser.getName(), actualUser.getName());
        assertEquals(newUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void getById_whenInvoke_thenReturnUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User actualUser = userService.getById(anyLong());

        assertEquals(user, actualUser);
    }

    @Test
    void getById_whenFindUserByIdEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.getById(anyLong()));
        assertEquals("User with 0 id not found.", e.getMessage());
    }

    @Test
    void delete_whenInvoked() {
        userService.delete(anyLong());

        verify(userRepository).deleteById(anyLong());
    }
}