package ru.practicum.shareit.item.repository.inmemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.user.repository.jpa.JpaUserRepository;

import java.util.*;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final JpaUserRepository userRepository;
    private final Map<Long, List<Item>> items = new HashMap<>();
    private int itemId = 0;

    @Override
    public Item create(Item item, long userId) {
        item.setId(++itemId);
        item.setOwner(userRepository.getById(userId));

        items.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        log.debug("Добавление предмета: {}", item);
        int index = findItemIndexInList(itemId, userId);
        return items.get(userId).get(index);
    }

    @Override
    public Item update(Item item, long userId) {
        log.debug("Обновление предмета: {}", item);

        if (userId != item.getOwner().getId()) {
            throw new EntityNotFoundException("ID владельца неверен!");
        }

        int index = findItemIndexInList(item.getId(), userId);
        if (index == -1) {
            throw new EntityNotFoundException("Предмет не найден для пользователя с ID: " + userId);
        }

        items.get(userId).set(index, item);
        return items.get(userId).get(index);
    }

    @Override
    public Item getItemById(long itemId) {
        log.debug("Получение предмета по ID: {}", itemId);
        for (Map.Entry<Long, List<Item>> entry : items.entrySet()) {
            Optional<Item> foundItem = entry.getValue().stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst();
            if (foundItem.isPresent()) {
                return foundItem.get();
            }
        }
        log.warn("Предмет с ID {} не найден.", itemId);
        return null;
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        log.debug("Получение всех предметов для пользователя с ID: {}", userId);
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        log.debug("Поиск предметов с текстом: {}", text);
        List<Item> availableItems = new ArrayList<>();
        String searchText = text.toLowerCase();

        for (List<Item> userItems : items.values()) {
            userItems.stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getDescription().toLowerCase().contains(searchText))
                    .forEach(availableItems::add);
        }

        log.debug("Найдено {} доступных предметов по запросу.", availableItems.size());
        return availableItems;
    }

    private int findItemIndexInList(long itemId, long userId) {
        List<Item> userItems = items.get(userId);
        if (userItems == null) {
            return -1;
        }

        return IntStream.range(0, userItems.size())
                .filter(i -> userItems.get(i).getId() == itemId)
                .findFirst()
                .orElse(-1);
    }
}

