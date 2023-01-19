package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validationInterface.Create;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@Validated({Create.class}) @RequestBody BookingShortDto bookingShortDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.create(BookingMapper.toBooking(bookingShortDto),
                userId, bookingShortDto.getItemId())));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> confirmRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable("bookingId") Long id,
                                                     @RequestParam(value = "approved") Boolean isApproved) {
        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.confirmRequest(userId, id, isApproved)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long id) {
        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.getById(userId, id)));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                                         @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(BookingMapper.toListBookingDto(bookingService.getAllByUser(userId, state, from, size)));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                          @RequestParam(value = "from", defaultValue = "0") int from,
                                                          @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(BookingMapper.toListBookingDto(bookingService.getAllByOwner(userId, state, from, size)));
    }
}
