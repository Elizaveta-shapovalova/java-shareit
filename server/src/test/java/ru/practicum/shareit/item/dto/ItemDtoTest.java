package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ItemDto.BookingDto lastBooking = new ItemDto.BookingDto(1L, LocalDateTime.now(), LocalDateTime.now(), 3L);
        ItemDto.BookingDto nextBooking = new ItemDto.BookingDto(2L, LocalDateTime.now(), LocalDateTime.now(), 3L);
        CommentDto commentDto = new CommentDto(1L, "test", "test", LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1L, "test", "test", true, 2L, lastBooking, nextBooking, Set.of(commentDto));

        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNotEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.comments.size()").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].created").isNotBlank();
    }
}