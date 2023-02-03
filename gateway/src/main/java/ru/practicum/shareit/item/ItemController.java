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
import ru.practicum.shareit.validationInterface.Create;
import ru.practicum.shareit.validationInterface.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody ItemRequestDto requestDto) {
        return itemClient.create(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Update.class}) @RequestBody ItemRequestDto requestDto,
                                         @PathVariable("itemId") Long id) {
        return itemClient.update(userId, requestDto, id);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable("itemId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
        if (text.isBlank()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return itemClient.search(text, from, size);
        }
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> commented(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Validated({Create.class}) @RequestBody CommentRequestDto commentRequestDto) {
        return itemClient.commented(commentRequestDto, itemId, userId);
    }
}
