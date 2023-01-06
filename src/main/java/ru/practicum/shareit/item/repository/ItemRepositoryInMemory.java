package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryInMemory {
    Item create(Item item);

    Item update(Item item, Long id);

    Optional<Item> getById(Long id);

    List<Item> getAll(Long userId);

    List<Item> search(String text);
}
