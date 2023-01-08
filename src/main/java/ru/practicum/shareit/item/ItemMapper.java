package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
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
                .comments(item.getComments() != null ? CommentMapper.toListCommentDto(item.getComments()) : null)
                .build();
    }

    public static ItemDto toItemDtoWithBooking(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .lastBooking(item.getLastBooking() != null ?
                        new ItemDto.BookingDto(item.getLastBooking().getId(),
                                item.getLastBooking().getStart(), item.getLastBooking().getEnd(),
                                item.getLastBooking().getBooker().getId()) : null)
                .nextBooking(item.getNextBooking() != null ?
                        new ItemDto.BookingDto(item.getNextBooking().getId(),
                                item.getNextBooking().getStart(), item.getNextBooking().getEnd(),
                                item.getNextBooking().getBooker().getId()) : null)
                .comments(item.getComments() != null ? CommentMapper.toListCommentDto(item.getComments()) : null)
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

    public static List<ItemDto> toListItemDtoWithBooking(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDtoWithBooking).collect(Collectors.toList());
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
