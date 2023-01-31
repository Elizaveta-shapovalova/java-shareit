package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    final ItemDto itemDto = ItemDto.builder()
            .name("test")
            .description("test")
            .available(true)
            .build();
    final Item item = new Item(1L, "test", "test", true, new User(), null, null,
            null, null);

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnResponseStatusOkWithItemInBody() {
        when(itemService.create(any(), anyLong())).thenReturn(item);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());

        verify(itemService).create(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenReturnResponseStatusOkWithItemInBody() {
        Item updatedItem = new Item(1L, "update", "update", false, new User(), null,
                new Booking(), new Booking(), null);
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(updatedItem);

        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());

        verify(itemService).update(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void update_whenUncorrectedId_thenThrowNotFoundException() {
        when(itemService.update(any(), anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).update(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenReturnResponseStatusOkWithItemInBody() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());

        verify(itemService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getById_whenUncorrectedId_thenThrowNotFoundException() {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithCollectionItemsInBody() {
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(item.getId()))
                .andExpect(jsonPath("$.[0].description").value(item.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(item.getAvailable()))
                .andExpect(jsonPath("$.[0].name").value(item.getName()))
                .andExpect(jsonPath("$.[0].requestId").isEmpty())
                .andExpect(jsonPath("$.[0].lastBooking").isEmpty())
                .andExpect(jsonPath("$.[0].nextBooking").isEmpty())
                .andExpect(jsonPath("$.[0].comments").isEmpty());

        verify(itemService).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionItemsInBody() {
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(itemService).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAll_whenUncorrectedPageMarkFrom_thenBadRequestReturned() {
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAll_whenUncorrectedPageMarkSize_thenBadRequestReturned() {
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void search_whenInvoked_thenReturnResponseStatusOkWithEmptyCollectionItemsInBody() {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "kill_me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(itemService).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void search_whenInvoked_thenReturnResponseStatusOkWithCollectionItemsInBody() {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(item));

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "kill_me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(item.getId()))
                .andExpect(jsonPath("$.[0].description").value(item.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(item.getAvailable()))
                .andExpect(jsonPath("$.[0].name").value(item.getName()))
                .andExpect(jsonPath("$.[0].requestId").isEmpty())
                .andExpect(jsonPath("$.[0].lastBooking").isEmpty())
                .andExpect(jsonPath("$.[0].nextBooking").isEmpty())
                .andExpect(jsonPath("$.[0].comments").isEmpty());

        verify(itemService).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void search_whenUncorrectedPageMarkFrom_thenBadRequestReturned() {
        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void search_whenUncorrectedPageMarkSize_thenBadRequestReturned() {
        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void commented() {
        CommentShortDto commentShortDto = new CommentShortDto("test");
        Comment comment = new Comment(1L, "test", item, User.builder().name("test").build(),
                LocalDateTime.now().plusHours(1));
        when(itemService.commented(any(), anyLong(), anyLong())).thenReturn(comment);

        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentShortDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthor().getName()))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(itemService).commented(any(), anyLong(), anyLong());
    }
}