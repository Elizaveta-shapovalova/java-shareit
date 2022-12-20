package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    User create(User user);

    User update(User user, Long id);

    void delete(Long id);

    Optional<User> getById(Long id);

    boolean isEmailContains(String email);
}
