package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingShortDtoTest {
    @Autowired
    private JacksonTester<BookingShortDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        BookingShortDto bookingShortDto = new BookingShortDto(LocalDateTime.now(), LocalDateTime.now(), 1L);

        JsonContent<BookingShortDto> result = jacksonTester.write(bookingShortDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
