package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private final Map<Long, List<Item>> items = new HashMap<>();
    private int nextItemId = 0;

    @Override
    public Item create(Item item, long userId) {
        item.setId(++nextItemId);
        item.setOwner(userRepository.getById(userId));
        items.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        log.debug("Добавление предмета: {}", item);
        int index = findItemIndex(nextItemId, userId);
        return items.get(userId).get(index);
    }

    @Override
    public Item update(Item item, long userId) {
        log.debug("Обновление предмета: {}", item);
        if (userId != item.getOwner().getId()) {
            throw new EntityNotFoundException("Идентификатор владельца некорректен!");
        }

        int index = findItemIndex(item.getId(), userId);
        if (index == -1) {
            throw new EntityNotFoundException("Предмет с идентификатором " + item.getId() + " не найден.");
        }
        items.get(userId).set(index, item);
        return items.get(userId).get(index);
    }

    @Override
    public Item getItemById(long itemId) {
        log.debug("Получение предмета по идентификатору: {}", itemId);
        for (long userId : items.keySet()) {
            Optional<Item> foundItem = items.get(userId).stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst();
            if (foundItem.isPresent()) {
                return foundItem.get();
            }
        }
        return null;
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        log.debug("Поиск предметов по тексту: {}", text);
        String lowerCaseText = text.toLowerCase();
        Collection<Item> availableItems = new ArrayList<>();
        for (long userId : items.keySet()) {
            List<Item> userItems = items.get(userId);
            if (userItems != null) {
                List<Item> matchedItems = userItems.stream()
                        .filter(Item::getAvailable)
                        .filter(item -> {
                            boolean matches = item.getName().toLowerCase().contains(lowerCaseText) ||
                                    item.getDescription().toLowerCase().contains(lowerCaseText);
                            if (matches) {
                                log.debug("Предмет соответствует поиску: {}", item);
                            }
                            return matches;
                        })
                        .toList();
                availableItems.addAll(matchedItems);
            }
        }
        return availableItems;
    }

    /**
     * Ищет индекс предмета в списке пользователя по его идентификатору.
     *
     * @param itemId Идентификатор предмета.
     * @param userId Идентификатор пользователя.
     * @return Индекс предмета в списке или -1, если не найден.
     */
    private int findItemIndex(long itemId, long userId) {
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
