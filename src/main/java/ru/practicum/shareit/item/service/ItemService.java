package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Item item, Long id);

    Item getById(Long id, Long userId);

    List<Item> getAll(Long userId);

    List<Item> search(String text);

    Comment commented(Comment comment, Long itemId, Long authorId);
}
