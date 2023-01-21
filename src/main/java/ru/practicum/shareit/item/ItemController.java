package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long id) {
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto), id, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDtoWithBooking(itemService.getById(id, userId));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
        return ItemMapper.toListItemDtoWithBooking(itemService.getAll(userId, from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
        return ItemMapper.toListItemDto(itemService.search(text, from, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto commented(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                @Validated({Create.class}) @RequestBody CommentShortDto commentShortDto) {
        return CommentMapper.toCommentDto(itemService.commented(CommentMapper.toComment(commentShortDto),
                itemId, userId));
    }
}
