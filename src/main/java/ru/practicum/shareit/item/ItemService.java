package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Item item, Long id);

    Item getById(Long id);

    List<Item> getAll(Long userId);

    List<Item> search(String text);
}
