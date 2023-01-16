package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long id) {
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto), id));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDtoWithBooking(itemService.getById(id, userId));
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toListItemDtoWithBooking(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return ItemMapper.toListItemDto(itemService.search(text));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto commented(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                @Validated({Create.class}) @RequestBody CommentShortDto commentShortDto) {
        return CommentMapper.toCommentDto(itemService.commented(CommentMapper.toComment(commentShortDto), itemId, userId));
    }
}
