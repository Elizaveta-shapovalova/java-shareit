package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public Item create(Item item, Long userId) {
        User user = findUserById(userId);
        item.setOwner(user);
        if (item.getRequester() != null) {
            itemRequestRepository.findById(item.getRequester())
                    .orElseThrow(() -> new NotFoundException(String.format("Request with %d id not found.", item.getRequester())));
        }
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Item item, Long id, Long userId) {
        User user = findUserById(userId);
        Item itemToUpdate = findById(id);
        if (!user.getId().equals(itemToUpdate.getOwner().getId())) {
            throw new NotFoundException("Owners don't match.");
        }
        updateFields(item, itemToUpdate);
        return itemToUpdate;
    }

    @Override
    public Item getById(Long id, Long userId) {
        findUserById(userId);
        Item item = findById(id);
        loadComments(List.of(item));
        if (item.getOwner().getId().equals(userId)) {
            loadLastBooking(List.of(item));
            loadNextBooking(List.of(item));
        }
        return item;
    }

    @Override
    public List<Item> getAll(Long userId, int from, int size) {
        findUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size));
        loadComments(items);
        loadLastBooking(items);
        loadNextBooking(items);
        return items;
    }

    @Override
    public List<Item> search(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text, PageRequest.of(from / size, size));
    }

    @Transactional
    @Override
    public Comment commented(Comment comment, Long itemId, Long authorId) {
        User user = findUserById(authorId);
        Item item = findById(itemId);
        if (bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(user, item, Status.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Refused access to add comment.");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    private void loadComments(List<Item> items) {
        Map<Item, Set<Comment>> comments = commentRepository.findByItemIn(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toSet()));

        items.forEach(item -> item.setComments(comments.getOrDefault(item, Collections.emptySet())));
    }

    private void loadLastBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsLast =
                bookingRepository.findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(items, Status.APPROVED,
                                LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setLastBooking(bookingsLast.getOrDefault(item, List.of())
                .stream().findFirst().orElse(null)));
    }

    private void loadNextBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsNext =
                bookingRepository.findByItemInAndStatusEqualsAndStartAfterOrderByStart(items, Status.APPROVED,
                                LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setNextBooking(bookingsNext.getOrDefault(item, List.of())
                .stream().findFirst().orElse(null)));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with %d id not found.", userId)));
    }

    private Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with %d id not found.", id)));
    }

    private void updateFields(Item item, Item itemToUpdate) {
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
    }
}
