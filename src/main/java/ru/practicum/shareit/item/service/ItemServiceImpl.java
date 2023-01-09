package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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
    UserService userService;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Transactional
    @Override
    public Item create(Item item) {
        userService.getById(item.getOwner());
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Item item, Long id) {
        User user = userService.getById(item.getOwner());
        Item itemToUpdate = findById(id);
        if (!user.getId().equals(itemToUpdate.getOwner())) {
            throw new NotFoundException("Owners don't match.");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public Item getById(Long id, Long userId) {
        Item item = findById(id);
        userService.getById(userId);
        item.setComments(commentRepository.findAllByItem(item));
        if (item.getOwner().equals(userId)) {
            loadLastBooking(List.of(item));
            loadNextBooking(List.of(item));
        }
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        userService.getById(userId);
        List<Item> items = itemRepository.findAllByOwner(userId);
        loadComments(items);
        loadLastBooking(items);
        loadNextBooking(items);
        return items;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Transactional
    @Override
    public Comment commented(Comment comment, Long itemId, Long authorId) {
        User user = userService.getById(authorId);
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

        items.forEach(item -> item.setComments(comments.get(item) != null ? comments.get(item) : null));
    }

    private void loadLastBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsLast =
                bookingRepository.findByItemInAndStatusEqualsAndStartBeforeOrStartEquals(items, Status.APPROVED,
                                LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setLastBooking(bookingsLast.get(item) != null ? bookingsLast.get(item).get(0) : null));
    }

    private void loadNextBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsNext =
                bookingRepository.findByItemInAndStatusEqualsAndStartAfter(items, Status.APPROVED, LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setNextBooking(bookingsNext.get(item) != null ? bookingsNext.get(item).get(0) : null));
    }


    private Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with %d id not found.", id)));
    }
}
