package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime end;
    Item item;
    User booker;
    Status status;
}
