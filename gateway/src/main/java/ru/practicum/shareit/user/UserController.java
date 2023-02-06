package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

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
    public ResponseEntity<Object> create(@RequestBody @Valid UserRequestDto userRequestDto) {
        return userClient.create(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long id, @RequestBody UserRequestDto userRequestDto) {
        if (!userRequestDto.getEmail().isBlank() || userRequestDto.getEmail() != null) {
            if (!userRequestDto.getEmail().contains("@")) {
                throw new IllegalArgumentException();
            }
        }
        return userClient.update(userRequestDto, id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable("userId") Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long id) {
        return userClient.delete(id);
    }
}