package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validationInterface.Create;
import ru.practicum.shareit.validationInterface.Update;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemDto), userId)));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                          @PathVariable("itemId") Long id) {
        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto), id, userId)));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@PathVariable("itemId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(ItemMapper.toItemDtoWithBooking(itemService.getById(id, userId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") int from,
                                                @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(ItemMapper.toListItemDtoWithBooking(itemService.getAll(userId, from, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(value = "from", defaultValue = "0") int from,
                                                @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(ItemMapper.toListItemDto(itemService.search(text, from, size)));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> commented(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Validated({Create.class}) @RequestBody CommentShortDto commentShortDto) {
        return ResponseEntity.ok(CommentMapper.toCommentDto(itemService.commented(CommentMapper.toComment(commentShortDto),
                itemId, userId)));
    }
}
