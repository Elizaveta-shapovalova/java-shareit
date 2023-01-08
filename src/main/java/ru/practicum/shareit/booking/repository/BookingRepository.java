package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime time, LocalDateTime timeNow, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Sort sort);

    List<Booking> findAllByItemOwner(Long owner, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(Long owner, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(Long owner, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(Long owner, LocalDateTime time, LocalDateTime timeNow, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(Long owner, Status status, Sort sort);

    Booking findFirstByItemIdAndStatusEqualsOrderByStart(Long itemId, Status status);

    Booking findFirstByItemIdAndStatusEqualsOrderByStartDesc(Long itemId, Status status);

    List<Booking> findAllByBookerAndItemAndStatusEqualsAndEndBefore(User booker, Item item, Status status, LocalDateTime time);

}
