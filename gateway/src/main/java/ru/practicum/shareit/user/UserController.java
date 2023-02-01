package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validationInterface.Create;
import ru.practicum.shareit.validationInterface.Update;

@Controller
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserRequestDto userRequestDto) {
        return userClient.create(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Validated({Update.class}) @RequestBody UserRequestDto userRequestDto,
                                         @PathVariable("userId") Long id) {
        return userClient.update(userRequestDto, id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long id) {
        return userClient.delete(id);
    }
}
