package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();

    User create(User user);

    User update(User user, Long id);

    User getById(Long id);

    void delete(Long id);
}
