package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    final User user = new User(1L, "test@mail.ru", "test");
    final Item item = Item.builder().requester(1L).name("test").build();
    final ItemRequest request = new ItemRequest(1L, "test", user, LocalDateTime.now(), null);

    @Test
    void create_whenInvoked_thenReturnRequestWithUserSet() {
        ItemRequest requestWithoutUser = ItemRequest.builder().description("test").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(request);

        ItemRequest actualRequest = itemRequestService.create(user.getId(), requestWithoutUser);

        assertEquals(request, actualRequest);
        assertEquals(request.getRequester(), actualRequest.getRequester());
        verify(itemRequestRepository).save(any());
    }

    @Test
    void create_whenFindUserByIdEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), request));
        assertEquals("User with 1 id not found.", e.getMessage());

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getAllByUser_whenInvoked_thenReturnCollectionRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(anyLong())).thenReturn(List.of(request));

        List<ItemRequest> actualRequests = itemRequestService.getAllByUser(user.getId());

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());
        assertEquals(request.getId(), actualRequests.get(0).getId());
        assertEquals(request.getRequester(), actualRequests.get(0).getRequester());
        assertTrue(actualRequests.get(0).getItems().isEmpty());
    }

    @Test
    void getAllByUser_whenRequestsHaveItems_thenReturnCollectionRequestsWithCollectionItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findByRequesterIn(anyCollection())).thenReturn(Set.of(item));

        List<ItemRequest> actualRequests = itemRequestService.getAllByUser(user.getId());

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());
        assertEquals(request.getId(), actualRequests.get(0).getId());
        assertEquals(request.getRequester(), actualRequests.get(0).getRequester());
        assertEquals(Set.of(item), actualRequests.get(0).getItems());
    }

    @Test
    void getAllByUser_whenInvokedCollectionRequestsEmpty_thenReturnEmptyCollectionRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(anyLong())).thenReturn(List.of());

        List<ItemRequest> actualRequests = itemRequestService.getAllByUser(user.getId());

        assertTrue(actualRequests.isEmpty());
    }

    @Test
    void getAll_whenNotValidPageMark_thenValidationExceptionThrown() {
        ValidationException e = assertThrows(ValidationException.class, () -> itemRequestService.getAll(user.getId(), -1, 0));
        assertEquals("Uncorrected numbering of page: from -1, size 0", e.getMessage());
    }

    @Test
    void getAll_whenInvoked_thenReturnCollectionRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNotLikeOrderByCreated(anyLong(), any()))
                .thenReturn(List.of(request));

        List<ItemRequest> actualRequests = itemRequestService.getAll(user.getId(), 0, 1);

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());
        assertEquals(request.getId(), actualRequests.get(0).getId());
        assertTrue(actualRequests.get(0).getItems().isEmpty());
    }

    @Test
    void getAll_whenInvokedCollectionRequestsEmpty_thenReturnEmptyCollectionRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNotLikeOrderByCreated(anyLong(), any()))
                .thenReturn(List.of());

        List<ItemRequest> actualRequests = itemRequestService.getAll(user.getId(), 0, 1);

        assertTrue(actualRequests.isEmpty());
    }

    @Test
    void getAll_whenRequestsHaveItems_thenReturnCollectionRequestsWithCollectionItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNotLikeOrderByCreated(anyLong(), any())).thenReturn(List.of(request));
        when(itemRepository.findByRequesterIn(anyCollection())).thenReturn(Set.of(item));

        List<ItemRequest> actualRequests = itemRequestService.getAll(user.getId(), 0, 1);

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());
        assertEquals(request.getId(), actualRequests.get(0).getId());
        assertEquals(request.getRequester(), actualRequests.get(0).getRequester());
        assertEquals(Set.of(item), actualRequests.get(0).getItems());
    }

    @Test
    void getById_whenInvoked_thenReturnRequestWithoutItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequest actualItemRequest = itemRequestService.getById(user.getId(), anyLong());

        assertEquals(request, actualItemRequest);
        assertTrue(actualItemRequest.getItems().isEmpty());
    }

    @Test
    void getById_whenFindRequestByIdIsEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemRequestService.getById(user.getId(), anyLong()));
        assertEquals("Request with 0 id not found.", e.getMessage());
    }

    @Test
    void getById_whenFindUserByIdIsEmpty_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> itemRequestService.getById(user.getId(), anyLong()));
        assertEquals("User with 1 id not found.", e.getMessage());
    }

    @Test
    void getById_whenRequestHasItems_thenReturnRequestWithCollectionItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequesterIn(anyCollection())).thenReturn(Set.of(item));

        ItemRequest actualItemRequest = itemRequestService.getById(user.getId(), anyLong());

        assertEquals(request, actualItemRequest);
        assertEquals(Set.of(item), actualItemRequest.getItems());
    }
}