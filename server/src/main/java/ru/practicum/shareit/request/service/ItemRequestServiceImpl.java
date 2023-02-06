package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public ItemRequest create(Long userId, ItemRequest itemRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать запрос - " +
                        "не найден пользователь с id " + userId));
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return itemRequest;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequest> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запросы пользователя - " +
                        "не найден пользователь с id " + userId));
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(this::setItemsToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequest> getAll(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запросы - " +
                        "не найден пользователь с id " + userId));
        return itemRequestRepository.findAllByRequestorIsNot(user,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(this::setItemsToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequest getById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запрос - " +
                        "не найден пользователь с id " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запрос - " +
                        "не существует запроса с id " + requestId));
        setItemsToItemRequestDto(itemRequest);

        return itemRequest;
    }

    private ItemRequest setItemsToItemRequestDto(ItemRequest itemRequest) {
        itemRequest.setItems(new HashSet<>(itemRepository.findAllByRequestId(itemRequest.getId())));
        return itemRequest;
    }
}
