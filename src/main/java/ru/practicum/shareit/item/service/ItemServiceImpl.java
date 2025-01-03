package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.inmemory.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        validateUserId(userId);
        validateItemDto(itemDto);
        log.debug("Создание элемента предмета: {}; для пользователя {}", itemDto, userId);
        Item item = convertToItem(itemDto);
        Item createdItem = itemRepository.create(item, userId);
        return convertToItemDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        validateUserId(userId);
        validateItemId(itemDto.getId());
        log.debug("Обновление элемента предмета: {}; для пользователя {}", itemDto, userId);
        Item existingItem = itemRepository.getItemById(itemDto.getId());
        Item updatedItem = itemRepository.update(convertForUpdate(itemDto, existingItem), userId);
        return convertToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        validateUserId(userId);
        validateItemId(itemId);
        log.debug("Получение элемента предмета по ID: {}; для пользователя {}", itemId, userId);
        Item item = itemRepository.getItemById(itemId);
        return convertToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        validateUserId(userId);
        log.debug("Получение предметов по ID пользователя: {}", userId);
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        log.debug("Поиск предметов по тексту: {}", text);
        return itemRepository.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        return null;
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        return null;
    }

    private void validateUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователь с header-Id: " + userId + " не существует.");
        }
        boolean userExists = userService.getAll().stream()
                .map(UserDto::getId)
                .anyMatch(id -> id.equals(userId));
        if (!userExists) {
            throw new EntityNotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
    }

    private void validateItemId(long itemId) {
        if (itemRepository.getItemById(itemId) == null) {
            throw new EntityNotFoundException("Элемент предмета с ID: " + itemId + " не найден.");
        }
    }

    private void validateItemDto(ItemDto itemDto) {if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
        throw new EmptyFieldException("Поля в элементе ItemDto содержат null!");
    }
        if (itemDto.getName().trim().isEmpty() || itemDto.getDescription().trim().isEmpty()) {
            throw new EmptyFieldException("Поля в элементе ItemDto пустые!");
        }
    }

    private Item convertToItem(ItemDto itemDto) {
        return ItemMapper.toItem(itemDto);
    }

    private Item convertForUpdate(ItemDto itemDto, Item existingItem) {
        return ItemMapper.toItemUpdate(itemDto, existingItem);
    }

    private ItemDto convertToItemDto(Item item) {
        return ItemMapper.toItemDto(item);
    }
}
