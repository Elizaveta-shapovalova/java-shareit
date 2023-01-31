package ru.practicum.shareit.customConverter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.ValidationException;

public class StringToEnumConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source.toUpperCase());
        } catch (Throwable e) {
            throw new ValidationException(String.format("Unknown state: %S", source));
        }
    }
}
