package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.annotation.TimeCrossingValid;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TimeCrossingValid(groups = {Create.class})
public class BookItemRequestDto {
    Long itemId;
    @FutureOrPresent(groups = {Create.class})
    LocalDateTime start;
    @Future(groups = {Create.class})
    LocalDateTime end;
}