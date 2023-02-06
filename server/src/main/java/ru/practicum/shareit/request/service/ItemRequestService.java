package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(Long userId, ItemRequest itemRequest);

    List<ItemRequest> getAllByUser(Long userId);

    List<ItemRequest> getAll(Long userId, int from, int size);

    ItemRequest getById(Long requestId, Long userId);
}
