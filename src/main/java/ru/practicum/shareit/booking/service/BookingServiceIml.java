package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
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
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceIml implements BookingService {
    BookingRepository bookingRepository;
    UserService userService;
    ItemRepository itemRepository;
    static Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
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
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
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
        return booking;
    }

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

    @Override
    public List<Booking> getAllByUser(Long userId, State state) {

        User booker = userService.getById(userId);
        List<Booking> bookings = List.of();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(booker, SORT_BY_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(), SORT_BY_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(), SORT_BY_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(booker, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_DESC);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatusEquals(booker, Status.WAITING, SORT_BY_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatusEquals(booker, Status.REJECTED, SORT_BY_DESC);
                break;
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, State state) {
        userService.getById(userId);
        List<Booking> bookings = List.of();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(userId, SORT_BY_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(userId, LocalDateTime.now(), SORT_BY_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(userId, LocalDateTime.now(), SORT_BY_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_DESC);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(userId, Status.WAITING, SORT_BY_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(userId, Status.REJECTED, SORT_BY_DESC);
                break;
        }
        return bookings;
    }
}
