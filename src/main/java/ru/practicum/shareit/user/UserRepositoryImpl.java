package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserRepository {
    final Map<Long, User> users = new HashMap<>();
    Long baseId = 0L;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(++baseId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Long id) {
        if (user.getName() != null) {
            users.get(id).setName(user.getName());
        }
        if (user.getEmail() != null) {
            users.get(id).setEmail(user.getEmail());
        }
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public boolean isExist(Long id) {
        return users.containsKey(id);
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
