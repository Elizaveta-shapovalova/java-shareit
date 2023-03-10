package ru.practicum.shareit.annotation;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<TimeCrossingValid, BookItemRequestDto> {
    @Override
    public void initialize(TimeCrossingValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookItemRequestDto bookingShortDto, ConstraintValidatorContext cxt) {
        LocalDateTime start = bookingShortDto.getStart();
        LocalDateTime end = bookingShortDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}