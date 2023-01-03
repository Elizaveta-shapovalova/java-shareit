package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validationInterface.Create;
import ru.practicum.shareit.validationInterface.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    String email;
    @NotBlank(groups = {Create.class})
    String name;
}
