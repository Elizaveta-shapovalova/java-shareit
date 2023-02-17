package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    final User user = new User(1L, "test@mail.ru", "test");
    final Item item = new Item(1L, "test", "test", true, user, 2L, null, null, null);
    final Comment comment = new Comment(1L, "test", item, user, LocalDateTime.now());
    final Booking booking = Booking.builder().id(5L).item(item).build();

    @Test
    void create_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.create(item, user.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());

        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_whenInvoked_thenReturnItem() {
        Item itemWithoutUser = Item.builder().name("test").description("test").available(true).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        Item actualItem = itemService.create(itemWithoutUser, user.getId());

        assertEquals(item, actualItem);
        assertEquals(user, actualItem.getOwner());
        verify(itemRepository).save(any());
    }

    @Test
    void create_whenFindItemRequestEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.create(item, user.getId()));
        assertEquals("Request with 2 id not found.", e.getMessage());

        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.update(item, item.getId(), user.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void update_whenFindItemEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.update(item, item.getId(), user.getId()));
        assertEquals("Item with 1 id not found.", e.getMessage());
    }

    @Test
    void update_whenUserIdAndOwnerIdDontMatch_thenNotFoundExceptionThrown() {
        User newUser = User.builder().id(2L).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.update(item, item.getId(), user.getId()));
        assertEquals("Owners don't match.", e.getMessage());
    }

    @Test
    void update_whenInvoked_thenReturnItem() {
        Item newItem = new Item(1L, "name", "desc", false, user, 2L, null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item actualItem = itemService.update(newItem, item.getId(), user.getId());

        assertEquals(newItem.getName(), actualItem.getName());
        assertEquals(newItem.getDescription(), actualItem.getDescription());
        assertEquals(newItem.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void getById_whenFindItemEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.getById(item.getId(), user.getId()));
        assertEquals("Item with 1 id not found.", e.getMessage());
    }

    @Test
    void getById_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.update(item, item.getId(), user.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getById_whenInvoked_thenReturnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Item actualItem = itemService.getById(item.getId(), user.getId());

        assertEquals(item, actualItem);
        assertTrue(actualItem.getComments().isEmpty());
        assertNull(actualItem.getLastBooking());
        assertNull(actualItem.getNextBooking());
    }

    @Test
    void getById_whenInvoked_thenReturnItemWithCollectionCommentsAndBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.findByItemIn(anyList())).thenReturn(Set.of(comment));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(anyList(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartAfterOrderByStart(anyList(), any(), any()))
                .thenReturn(List.of(booking));

        Item actualItem = itemService.getById(item.getId(), user.getId());

        assertEquals(item, actualItem);
        assertNotNull(actualItem.getComments());
        assertEquals(Set.of(comment), actualItem.getComments());
        assertNotNull(actualItem.getLastBooking());
        assertEquals(booking, actualItem.getLastBooking());
        assertNotNull(actualItem.getNextBooking());
        assertEquals(booking, actualItem.getNextBooking());
    }

    @Test
    void getAll_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.getAll(user.getId(), 1, 1));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getAll_whenInvoked_thenReturnEmptyCollectionItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any())).thenReturn(List.of());

        List<Item> actualItems = itemService.getAll(user.getId(), 0, 1);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void getAll_whenInvoked_thenReturnCollectionItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any())).thenReturn(List.of(item));

        List<Item> actualItems = itemService.getAll(user.getId(), 0, 1);

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
    }

    @Test
    void search_whenInvoked_thenReturnEmptyCollectionItems() {
        List<Item> items = itemService.search("", 0, 1);

        assertTrue(items.isEmpty());
    }

    @Test
    void search_whenInvoked_thenReturnCollectionItems() {
        when(itemRepository.search(anyString(), any())).thenReturn(List.of(item));

        List<Item> actualItems = itemService.search("test", 0, 1);

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());
        assertEquals(List.of(item), actualItems);
    }

    @Test
    void commented_whenFindUserEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.commented(comment, item.getId(), user.getId()));
        assertEquals("User with 1 id not found.", e.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void commented_whenFindItemEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.commented(comment, item.getId(), user.getId()));
        assertEquals("Item with 1 id not found.", e.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void commented_whenFindBookerEmpty_thenValidationExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(any(), any(), any(), any()))
                .thenReturn(List.of());

        ValidationException e = assertThrows(ValidationException.class, () -> itemService.commented(comment, item.getId(), user.getId()));
        assertEquals("Refused access to add comment.", e.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void commented_whenInvoked_thenReturnComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Comment actualComment = itemService.commented(comment, item.getId(), user.getId());

        assertEquals(comment, actualComment);
        verify(commentRepository).save(any());
    }
}