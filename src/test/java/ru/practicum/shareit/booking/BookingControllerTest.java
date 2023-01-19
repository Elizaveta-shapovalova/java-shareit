package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    final BookingShortDto bookingShortDto = BookingShortDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .build();
    final Booking booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
            Item.builder().id(1L).name("test").owner(User.builder().id(2L).build()).build(),
            User.builder().id(1L).name("test").build(),
            Status.WAITING);

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnResponseStatusOkWithBookingInBody() {
        when(bookingService.create(any(), anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));

        verify(bookingService).create(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenTimeCrossing_thenReturnResponseStatusBadRequest() {
        bookingShortDto.setEnd(LocalDateTime.now().plusMinutes(1));
        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void confirmRequest_whenInvoked_thenReturnResponseStatusOkWithBookingInBody() {
        booking.setStatus(Status.APPROVED);
        when(bookingService.confirmRequest(any(), anyLong(), any())).thenReturn(booking);


        mvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));

        verify(bookingService).confirmRequest(any(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenReturnResponseStatusOkWithBookingInBody() {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));


        verify(bookingService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getById_whenUncorrectedId_thenThrowNotFoundException() {
        when(bookingService.getById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenInvoked_thenReturnResponseStatusOkWithCollectionBookingsInBody() {
        when(bookingService.getAllByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty())
                .andExpect(jsonPath("$.[0].status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(booking.getItem().getName()));


        verify(bookingService).getAllByUser(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenUncorrectedState_thenReturnResponseStatusBadRequest() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "Unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenStateToLowCase_thenReturnResponseStatusOk() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionBookingsInBody() {
        when(bookingService.getAllByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(bookingService).getAllByUser(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_whenInvoked_thenReturnResponseStatusOkWithCollectionBookingsInBody() {
        when(bookingService.getAllByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty())
                .andExpect(jsonPath("$.[0].status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(booking.getItem().getName()));

        verify(bookingService).getAllByOwner(anyLong(), any(), anyInt(), anyInt());
    }
}