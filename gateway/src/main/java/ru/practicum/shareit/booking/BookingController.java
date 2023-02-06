package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
    BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody BookItemRequestDto requestDto) {
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId) {

        return bookingClient.getById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.confirmRequest(userId, bookingId, approved);
    }
}