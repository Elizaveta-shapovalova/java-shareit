package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking, Long userId, Long itemId);

    Booking confirmRequest(Long userId, Long id, Boolean isApproved);

    Booking getById(Long userId, Long id);

    List<Booking> getAllByUser(Long userId, State state);

    List<Booking> getAllByOwner(Long userId, State state);
}
