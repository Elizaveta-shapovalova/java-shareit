package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User create(User user) {
        validationEmail(user.getEmail());
        return userRepository.create(user);
    }

    @Override
    public User update(User user, Long id) {
        User userToCheck = getById(id);
        if (user.getEmail() != null && !user.getEmail().equals(userToCheck.getEmail())) {
            validationEmail(user.getEmail());
        }
        return userRepository.update(user, id);
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id).orElseThrow(() -> new ObjectNotFoundException(String.format("User with %d id not found.", id)));
    }

    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.delete(id);
    }

    private void validationEmail(String email) {
        if (userRepository.isEmailContains(email)) {
            throw new ValidationException(String.format("User with %s email already exist.", email));
        }
    }
}
