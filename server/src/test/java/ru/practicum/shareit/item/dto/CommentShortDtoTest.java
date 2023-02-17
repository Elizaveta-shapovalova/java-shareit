package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentShortDtoTest {
    @Autowired
    private JacksonTester<CommentShortDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        CommentShortDto commentShortDto = new CommentShortDto("test");

        JsonContent<CommentShortDto> result = jacksonTester.write(commentShortDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test");
    }
}