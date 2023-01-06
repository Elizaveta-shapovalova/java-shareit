package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceIml implements BookingService {
    BookingRepository bookingRepository;
    UserService userService;
    ItemRepository itemRepository;
    static Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public Booking create(Booking booking, Long userId, Long itemId) {
        User user = userService.getById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with %d id not found.", itemId)));
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Item %s isn't available.", item.getName()));
        }
        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Refused access.");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException(String.format("Wrong timecodes. Start - %s, end - %s", booking.getStart(),
                    booking.getEnd()));
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking confirmRequest(Long userId, Long id, Boolean isApproved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with %d id not found.", id)));
        userService.getById(userId);
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException("Refused access.");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException(String.format("Booking has %s already.", booking.getStatus()));
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with %d id not found.", id)));
        userService.getById(userId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException("Refused access. User or Owner don't match.");
        }
        return booking;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getAllByUser(Long userId, String state) {
        User booker = userService.getById(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBooker(booker, SORT_BY_DESC));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(booker, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(booker, Status.WAITING, SORT_BY_DESC));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(booker, Status.REJECTED, SORT_BY_DESC));
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %S", state));
        }
        return bookings;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getAllByOwner(Long userId, String state) {
        userService.getById(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByItemOwner(userId, SORT_BY_DESC));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(userId, LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(userId, LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_DESC));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(userId, Status.WAITING, SORT_BY_DESC));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(userId, Status.REJECTED, SORT_BY_DESC));
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %S", state));
        }
        return bookings;
    }
}
