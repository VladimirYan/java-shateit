package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    private static final String owner = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestHeader(owner) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи пользователем с ownerId: {}", userId);
        return itemClient.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Valid @PathVariable Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(owner) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление отзыва пользователем");
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
