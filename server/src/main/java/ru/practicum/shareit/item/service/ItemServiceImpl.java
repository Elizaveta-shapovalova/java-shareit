package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> getAll(Long userId, int from, int size) {
        return itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size))
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(this::setFieldsToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Item getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        item.setComments(commentRepository.findAllByItemId(id));
        if (item.getOwner().getId().equals(ownerId)) {
            setFieldsToItemDto(item);
        }

        return item;
    }

    @Transactional
    @Override
    public Item create(Item item, Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать вещь - " +
                        "не найден пользователь с id: " + userId));
        item.setOwner(user);
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Невозможно создать вещь - " +
                            "не найден запрос с id: " + requestId));
            item.setRequest(itemRequest);
        }
        itemRepository.save(item);

        return item;
    }

    @Transactional
    @Override
    public Item update(Item item, Long id, Long userId) {
        Item itemTo = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Невозможно обновить вещь - у пользователя с id: " + userId + "нет такой вещи");
        }
        Optional.ofNullable(item.getName()).ifPresent(itemTo::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(itemTo::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(itemTo::setAvailable);

        return itemRepository.save(itemTo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> search(String text, int from, int size) {
        List<Item> searchedItems = new ArrayList<>();
        if (text.isBlank()) {
            return searchedItems;
        }
        searchedItems = itemRepository.search(text, PageRequest.of(from / size, size))
                .stream()
                .collect(Collectors.toList());

        return searchedItems;
    }

    @Transactional
    @Override
    public Comment createComment(Long itemId, Long userId, Comment comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать комментарий - " +
                        "не существует пользователя с id " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать комментарий - " +
                        "не существует вещи с id " + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Невозможно создать комментарий - " +
                    "вещь не бралась пользователем в аренду или аренда вещи еще не завершена");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        commentRepository.save(comment);

        return comment;
    }

    private Item setFieldsToItemDto(Item item) {
        item.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId()).isEmpty() ?
                null : bookingRepository.findAllByItemIdOrderByStartAsc(item.getId()).get(0));
        item.setNextBooking(item.getLastBooking() == null ?
                null : bookingRepository.findAllByItemIdOrderByStartDesc(item.getId()).get(0));
        item.setComments(commentRepository.findAllByItemId(item.getId()));
        return item;
    }
}
