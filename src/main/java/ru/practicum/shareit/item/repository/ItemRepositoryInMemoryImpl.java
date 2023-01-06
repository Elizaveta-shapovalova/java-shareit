package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryInMemoryImpl implements ItemRepositoryInMemory {
    final Map<Long, Item> items = new HashMap<>();
    final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();
    Long baseId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(++baseId);
        final List<Item> itemsToAdd = userItemIndex.computeIfAbsent(item.getOwner(), k -> new ArrayList<>());
        itemsToAdd.add(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, Long id) {
        Item itemToUpdate = items.get(id);
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAll(Long userId) {
        return List.copyOf(userItemIndex.get(userId));
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(it -> it.getName().toLowerCase().contains(text.toLowerCase()) ||
                        it.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                                it.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
