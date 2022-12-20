package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validationInterface.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    String description;
    @NotNull(groups = {Create.class})
    Boolean available;
    Long owner;
    Long request;
}
