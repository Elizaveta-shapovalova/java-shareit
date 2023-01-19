package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceIml implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    static Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public Booking create(Booking booking, Long userId, Long itemId) {
        User user = findUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with %d id not found.", itemId)));
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Item %s isn't available.", item.getName()));
        }
        if (item.getOwner().getId().equals(userId)) {
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
        findUserById(userId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Refused access.");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException(String.format("Booking has %S already.", booking.getStatus()));
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
        findUserById(userId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Refused access. User or Owner don't match.");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByUser(Long userId, State state, int from, int size) {
        validatePageMark(from, size);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_DESC);
        User booker = findUserById(userId);
        List<Booking> bookings = List.of();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(booker, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(booker, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatusEquals(booker, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatusEquals(booker, Status.REJECTED, pageable);
                break;
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, State state, int from, int size) {
        validatePageMark(from, size);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_DESC);
        findUserById(userId);
        List<Booking> bookings = List.of();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, Status.REJECTED, pageable);
                break;
        }
        return bookings;
    }

    private void validatePageMark(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException(String.format("Uncorrected numbering of page: from %d, size %d", from, size));
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with %d id not found.", userId)));
    }
}
