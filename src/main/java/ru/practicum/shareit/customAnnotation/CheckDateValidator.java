package ru.practicum.shareit.customAnnotation;

import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<TimeCrossingValid, BookingShortDto> {
    @Override
    public void initialize(TimeCrossingValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingShortDto bookingShortDto, ConstraintValidatorContext cxt) {
        LocalDateTime start = bookingShortDto.getStart();
        LocalDateTime end = bookingShortDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
