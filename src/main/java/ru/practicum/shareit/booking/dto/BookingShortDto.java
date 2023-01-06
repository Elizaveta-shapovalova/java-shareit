package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.validationInterface.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingShortDto {
    Long id;
    @FutureOrPresent(groups = {Create.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(groups = {Create.class})
    LocalDateTime start;
    @Future(groups = {Create.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(groups = {Create.class})
    LocalDateTime end;
    @NotNull(groups = {Create.class})
    Long itemId;
    Long bookerId;
}
