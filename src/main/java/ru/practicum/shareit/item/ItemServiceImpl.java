package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserServiceImpl userRepository;


    @Override
    public Item create(Item item) {
        userRepository.getById(item.getOwner());
        return itemRepository.create(item);
    }

    @Override
    public Item update(Item item, Long id) {
        User user = userRepository.getById(item.getOwner());
        Item itemToCheck = getById(id);
        if(!user.getId().equals(itemToCheck.getOwner())) {
            throw new ObjectNotFoundException("Owners don't match.");
        }
        return itemRepository.update(item, id);
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.getById(id).orElseThrow(() -> new ObjectNotFoundException(String.format("Item with %d id not found.", id)));
    }

    @Override
    public List<Item> getAll(Long userId) {
        userRepository.getById(userId);
        return itemRepository.getAll(userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text);
    }
}
