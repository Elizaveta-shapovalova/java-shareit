package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryInMemoryImpl implements UserRepositoryInMemory {
    final Map<Long, User> users = new HashMap<>();
    Long baseId = 0L;

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(++baseId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Long id) {
        User userToUpdate = users.get(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean isEmailContains(String email) {
        for (User us : users.values()) {
            if (us.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}
