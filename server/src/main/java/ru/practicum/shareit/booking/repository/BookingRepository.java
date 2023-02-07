package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime time, LocalDateTime timeNow, Pageable pageable);

    List<Booking> findAllByBookerAndStatusEquals(User booker, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long owner, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long owner, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long owner, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long owner, LocalDateTime time, LocalDateTime timeNow, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long owner, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(List<Item> items, BookingStatus bookingStatus, LocalDateTime time);

    List<Booking> findByItemInAndStatusEqualsAndStartAfterOrderByStart(List<Item> items, BookingStatus bookingStatus, LocalDateTime time);

    List<Booking> findAllByBookerAndItemAndStatusEqualsAndEndBefore(User booker, Item item, BookingStatus bookingStatus, LocalDateTime time);
}
