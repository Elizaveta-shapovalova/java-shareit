package ru.practicum.shareit.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.State;

public class StringToEnumConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        return State.valueOf(source.toUpperCase());
    }
}
