package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll(Long userId, int from, int size);

    Item getById(Long id, Long ownerId);

    Item create(Item item, Long userId, Long requestId);

    Item update(Item item, Long id, Long userId);

    List<Item> search(String text, int from, int size);

    Comment createComment(Long itemId, Long userId, Comment comment);
}
