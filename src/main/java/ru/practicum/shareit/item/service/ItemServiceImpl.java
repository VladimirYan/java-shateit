package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.GatewayHeaderException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        checkUserId(userId);
        validateItemDto(itemDto);
        log.debug("Создание элемента предмета: {}; для пользователя {}", itemDto, userId);
        Item item = toItem(itemDto);
        Item createdItem = itemRepository.create(item, userId);
        return toItemDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        checkUserId(userId);
        checkItemId(itemDto.getId());
        log.debug("Обновление элемента предмета: {}; для пользователя {}", itemDto, userId);
        Item existingItem = itemRepository.getItemById(itemDto.getId());
        Item updatedItem = toItemUpdate(itemDto, existingItem);
        Item result = itemRepository.update(updatedItem, userId);
        return toItemDto(result);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        checkUserId(userId);
        checkItemId(itemId);
        log.debug("Получение элемента предмета по ID: {}; для пользователя {}", itemId, userId);
        Item item = itemRepository.getItemById(itemId);
        return toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        checkUserId(userId);
        log.debug("Получение предметов для пользователя с ID: {}", userId);
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        log.debug("Поиск предметов по запросу: {}", text);
        return itemRepository.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Поля в элементе ItemDto не могут быть null!");
        }
        if (itemDto.getName().trim().isEmpty() || itemDto.getDescription().trim().isEmpty()) {
            throw new EmptyFieldException("Поля 'name' и 'description' в элементе ItemDto не могут быть пустыми!");
        }
    }

    private void checkUserId(long userId) {
        if (userId <= 0) {
            throw new GatewayHeaderException("Некорректный ID пользователя в заголовке: " + userId);
        }
        boolean userExists = userService.getAll().stream()
                .anyMatch(user -> user.getId() == userId);
        if (!userExists) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }

    private void checkItemId(long itemId) {
        if (itemId <= 0) {
            throw new EntityNotFoundException("Некорректный ID предмета: " + itemId);
        }
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Предмет с ID " + itemId + " не найден.");
        }
    }
}
