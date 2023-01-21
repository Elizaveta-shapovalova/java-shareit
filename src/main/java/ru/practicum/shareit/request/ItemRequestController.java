package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validationInterface.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Validated({Create.class}) @RequestBody ItemRequestShortDto itemRequestShortDto) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.create(userId,
                ItemRequestMapper.toItemRequest(itemRequestShortDto)));
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toListItemRequestDtoWithItems(itemRequestService.getAllByUser(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
        return ItemRequestMapper.toListItemRequestDtoWithItems(itemRequestService.getAll(userId, from, size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("requestId") Long id) {
        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequestService.getById(userId, id));
    }
}
