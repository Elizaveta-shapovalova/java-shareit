package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .lastBooking(item.getLastBooking() != null ? BookingMapper.toBookingShortDto(item.getLastBooking()) : null)
                .nextBooking(item.getNextBooking() != null ? BookingMapper.toBookingShortDto(item.getNextBooking()) : null)
                .comments(item.getComments() != null ? item.getComments().stream().map(CommentMapper::toCommentDto)
                        .collect(Collectors.toSet()) : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest() != null ? itemDto.getRequest() : null)
                .build();
    }
}
