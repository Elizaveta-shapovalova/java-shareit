package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImlTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceIml bookingService;
    final User user = new User(1L, "test@mail.ru", "test");
    final Item item = new Item(1L, "test", "test", true, user, 2L, null, null, null);
    final Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user, null);

    @Test
    void create_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(booking, user.getId(), item.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenFindItemEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(booking, user.getId(), item.getId()));
        assertEquals("Item with 1 id not found.", e.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenItemAvailableFalse_thenValidationExceptionThrown() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException e = assertThrows(ValidationException.class, () -> bookingService.create(booking, user.getId(), item.getId()));
        assertEquals("Item test isn't available.", e.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenRefusedAccess_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(booking, user.getId(), item.getId()));
        assertEquals("Refused access.", e.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenInvoked_thenReturnBooking() {
        item.setOwner(User.builder().id(2L).build());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking actualBooking = bookingService.create(booking, user.getId(), item.getId());

        assertEquals(booking.getId(), actualBooking.getId());
        assertEquals(Status.WAITING, actualBooking.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void confirmRequest_whenFindBookingEmpty_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.confirmRequest(user.getId(), booking.getId(), true));
        assertEquals("Booking with 1 id not found.", e.getMessage());
    }

    @Test
    void confirmRequest_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.confirmRequest(user.getId(), booking.getId(), true));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void confirmRequest_whenRefusedAccess_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.confirmRequest(2L, booking.getId(), true));
        assertEquals("Refused access.", e.getMessage());
    }

    @Test
    void confirmRequest_whenBookingApproved_thenValidationExceptionThrown() {
        item.setOwner(User.builder().id(2L).build());
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ValidationException e = assertThrows(ValidationException.class, () -> bookingService.confirmRequest(2L, booking.getId(), true));
        assertEquals("Booking has APPROVED already.", e.getMessage());
    }

    @Test
    void confirmRequest_whenApproved_thenReturnBooking() {
        item.setOwner(User.builder().id(2L).build());
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking actualBooking = bookingService.confirmRequest(2L, booking.getId(), true);

        assertEquals(booking.getId(), actualBooking.getId());
        assertEquals(Status.APPROVED, actualBooking.getStatus());
    }

    @Test
    void confirmRequest_whenRejected_thenReturnBooking() {
        item.setOwner(User.builder().id(2L).build());
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking actualBooking = bookingService.confirmRequest(2L, booking.getId(), false);

        assertEquals(booking.getId(), actualBooking.getId());
        assertEquals(Status.REJECTED, actualBooking.getStatus());
    }

    @Test
    void getById_whenFindBookingEmpty_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getById(user.getId(), booking.getId()));
        assertEquals("Booking with 1 id not found.", e.getMessage());
    }

    @Test
    void getById_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getById(user.getId(), booking.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getById_whenRefusedAccess_thenNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getById(2L, booking.getId()));
        assertEquals("Refused access. User or Owner don't match.", e.getMessage());
    }

    @Test
    void getById_whenInvoked_thenReturnBooking() {
        item.setOwner(User.builder().id(2L).build());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking actualBooking = bookingService.getById(2L, booking.getId());

        assertEquals(booking, actualBooking);
    }

    @Test
    void getAllByUser_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getAllByUser(user.getId(), State.ALL, 0, 1));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getAllByUser_whenInvoked_thenReturnEmptyCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(any(), any())).thenReturn(List.of());

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.ALL, 0, 1);

        assertTrue(actualBookings.isEmpty());
    }

    @Test
    void getAllByUser_whenInvoked_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.ALL, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByUser_whenStatePast_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.PAST, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByUser_whenStateFuture_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.FUTURE, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByUser_whenStateCurrent_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(), any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.CURRENT, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByUser_whenStateWaiting_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.WAITING, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByUser_whenStateRejected_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByUser(user.getId(), State.REJECTED, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(user.getId(), State.ALL, 0, 1));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getAllByOwner_whenInvoked_thenReturnEmptyCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerId(any(), any())).thenReturn(List.of());

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.ALL, 0, 1);

        assertTrue(actualBookings.isEmpty());
    }

    @Test
    void getAllByOwner_whenInvoked_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.ALL, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStatePast_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.PAST, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateFuture_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.FUTURE, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateCurrent_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.CURRENT, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateWaiting_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.WAITING, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateRejected_thenReturnCollectionBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllByOwner(user.getId(), State.REJECTED, 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(booking), actualBookings);
    }
}