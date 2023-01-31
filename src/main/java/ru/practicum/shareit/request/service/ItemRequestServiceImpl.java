package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequest create(Long userId, ItemRequest itemRequest) {
        User user = findUserById(userId);
        itemRequest.setRequester(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllByUser(Long userId) {
        findUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreated(userId);
        loadItems(requests);
        return requests;
    }

    @Override
    public List<ItemRequest> getAll(Long userId, int from, int size) {
        findUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotLikeOrderByCreated(userId,
                PageRequest.of(from / size, size));
        loadItems(requests);
        return requests;
    }

    @Override
    public ItemRequest getById(Long userId, Long id) {
        findUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Request with %d id not found.", id)));
        loadItems(List.of(itemRequest));
        return itemRequest;
    }

    private void loadItems(List<ItemRequest> requests) {
        Map<Long, List<ItemRequest>> requestById = requests.stream().collect(groupingBy(ItemRequest::getId));

        Map<Long, Set<Item>> items = itemRepository.findByRequesterIn(requestById.keySet())
                .stream()
                .collect(groupingBy(Item::getRequester, toSet()));

        requests.forEach(itemRequest -> itemRequest.setItems(items.getOrDefault(itemRequest.getId(), Collections.emptySet())));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with %d id not found.", userId)));
    }
}
