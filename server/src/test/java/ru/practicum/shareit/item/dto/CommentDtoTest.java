package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        CommentDto commentDto = new CommentDto(1L, "test", "test", LocalDateTime.now());

        JsonContent<CommentDto> result = jacksonTester.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
    }
}