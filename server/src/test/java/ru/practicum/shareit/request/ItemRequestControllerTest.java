package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    final ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto("test");
    final ItemRequest itemRequest = new ItemRequest(1L, "test", new User(),
            LocalDateTime.now().plusHours(1), null);

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnResponseStatusOkWithRequestInBody() {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequest);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(itemRequestService).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionRequestsInBody() {
        when(itemRequestService.getAllByUser(anyLong())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(itemRequestService).getAllByUser(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenInvoked_thenReturnResponseStatusOkWithCollectionRequestsInBody() {
        when(itemRequestService.getAllByUser(anyLong())).thenReturn(List.of(itemRequest));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequest.getDescription()))
                .andExpect(jsonPath("$.[0].created").isNotEmpty())
                .andExpect(jsonPath("$.[0].items").isEmpty());

        verify(itemRequestService).getAllByUser(anyLong());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionRequestsInBody() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(itemRequestService).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithCollectionRequestsInBody() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequest.getDescription()))
                .andExpect(jsonPath("$.[0].created").isNotEmpty())
                .andExpect(jsonPath("$.[0].items").isEmpty());

        verify(itemRequestService).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAll_whenUncorrectedId_thenThrowNotFoundException() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getById_whenUncorrectedId_thenThrowNotFoundException() {
        when(itemRequestService.getById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenReturnResponseStatusOkWithRequestInBody() {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequest);

        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(itemRequestService).getById(anyLong(), anyLong());
    }
}
