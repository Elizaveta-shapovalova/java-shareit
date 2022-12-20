package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getAll();

    User create(User user);

    User update(User user, Long id);

    void delete(Long id);

    User getById(Long id);

    boolean isExist(Long id);

    boolean isEmailContains(String email);
}
