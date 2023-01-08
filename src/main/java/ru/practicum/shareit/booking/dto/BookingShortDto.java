package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.customAnnotation.TimeCrossingValid;
import ru.practicum.shareit.validationInterface.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@TimeCrossingValid(groups = {Create.class})
public class BookingShortDto {
    @FutureOrPresent(groups = {Create.class})
    LocalDateTime start;
    @Future(groups = {Create.class})
    LocalDateTime end;
    @NotNull(groups = {Create.class})
    Long itemId;
}
