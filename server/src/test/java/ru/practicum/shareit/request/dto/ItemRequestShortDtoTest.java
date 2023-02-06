package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestShortDtoTest {
    @Autowired
    private JacksonTester<ItemRequestShortDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto("test");

        JsonContent<ItemRequestShortDto> result = jacksonTester.write(itemRequestShortDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
    }
}