package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(Item item);

    Item update(Item item, Long id);

    Item getById(Long id);

    Collection<Item> getAll(Long userId);

    Collection<Item> search(String text);
}
