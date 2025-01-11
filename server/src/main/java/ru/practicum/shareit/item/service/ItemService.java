package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto item, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId, Long id);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> getItemsBySearchQuery(String text);

    void deleteItem(Long itemId, Long ownerId);

    void deleteItemsByOwnerId(Long ownerId);

    ItemDto getItemById(Long itemId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);
}