package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item, Long userId);

    Item update(Item item, Long id, Long userId);

    Item getById(Long id, Long userId);

    List<Item> getAll(Long userId, int from, int size);

    List<Item> search(String text, int from, int size);

    Comment commented(Comment comment, Long itemId, Long authorId);
}
