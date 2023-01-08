package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validationInterface.Create;
import ru.practicum.shareit.validationInterface.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return UserMapper.toListUserDto(userService.getAll());
    }

    @PostMapping
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({Update.class}) @RequestBody UserDto userDto, @PathVariable("userId") Long id) {
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto), id));
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") Long id) {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable("userId") Long id) {
        userService.delete(id);
    }
}
