package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
    Booker booker;
    Item item;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Booker {
        Long id;
        String name;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Item {
        Long id;
        String name;
    }
}
