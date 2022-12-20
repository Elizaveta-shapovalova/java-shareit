package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryImpl implements ItemRepository {
    final Map<Long, Item> items = new HashMap<>();
    Long baseId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(++baseId);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item, Long id) {
        if (item.getName() != null) {
            items.get(id).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(id).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(id).setAvailable(item.getAvailable());
        }
        return items.get(id);
    }

    @Override
    public Item getById(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> getAll(Long userId) {
        return items.values().stream().filter(it -> it.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return items.values().stream()
                .filter(it -> it.getName().toLowerCase().contains(text.toLowerCase()) ||
                        it.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(it -> it.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExist(Long id) {
        return items.containsKey(id);
    }

    @Override
    public boolean isOwner(Item item, Long id) {
        return items.get(id).getOwner().equals(item.getOwner());
    }
}
