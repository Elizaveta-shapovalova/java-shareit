package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validationInterface.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated({Create.class}) @RequestBody ItemRequestShortDto itemRequestShortDto) {
        return ResponseEntity.ok(ItemRequestMapper.toItemRequestDto(itemRequestService.create(userId,
                ItemRequestMapper.toItemRequest(itemRequestShortDto))));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(ItemRequestMapper.toListItemRequestDtoWithItems(itemRequestService.getAllByUser(userId)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(ItemRequestMapper.toListItemRequestDtoWithItems(itemRequestService.getAll(userId, from, size)));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable("requestId") Long id) {
        return ResponseEntity.ok(ItemRequestMapper.toItemRequestDtoWithItems(itemRequestService.getById(userId, id)));
    }
}
