package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder().id(1L).name("test").email("test@mail.ru").build());
        itemRepository.save(Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void search() {
        List<Item> items = itemRepository.search("test", PageRequest.of(0, 1));

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getId());
    }

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void findByRequesterIn() {
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }
}