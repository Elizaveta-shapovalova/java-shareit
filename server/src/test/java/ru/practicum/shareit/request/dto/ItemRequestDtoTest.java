package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ItemShortDto itemShortDto = ItemShortDto.builder().id(1L).name("test").description("test").available(true).requestId(1L).build();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "test", LocalDateTime.now(), Set.of(itemShortDto));

        JsonContent<ItemRequestDto> result = jacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathArrayValue("$.items").isNotEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.items.size()").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo("test");
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(1);
    }
}