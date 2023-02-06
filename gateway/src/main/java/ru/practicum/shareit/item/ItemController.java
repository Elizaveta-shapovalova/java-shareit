package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody ItemRequestDto requestDto) {
        return itemClient.create(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Update.class}) @RequestBody ItemRequestDto requestDto,
                                         @PathVariable Long itemId) {
        return itemClient.update(requestDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (text.isBlank()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        } else {
            return itemClient.search(text, from, size);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> commented(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Validated({Create.class}) @RequestBody CommentRequestDto requestDto) {
        return itemClient.commented(itemId, userId, requestDto);
    }
}
