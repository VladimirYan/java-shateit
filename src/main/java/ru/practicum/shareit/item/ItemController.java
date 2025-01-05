package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    private static final String owner = "X-Sharer-User-Id";

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @Valid @RequestHeader(owner) Long id) {
        log.info("Получен запрос на добавление вещи пользователем с ownerId: {}", id);
        return itemService.add(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody ItemDto itemDto,
            @Valid @RequestHeader(owner) Long ownerId,
            @Valid @PathVariable Long itemId) {
        return itemService.update(itemDto, ownerId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(owner) Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(owner) Long ownerId, @PathVariable Long itemId) {
        itemService.deleteItem(ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@Valid @PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        return itemService.getItemsBySearchQuery(text);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto, @RequestHeader(owner) Long userId,
                                 @PathVariable Long itemId) {
        log.info("Получен запрос на добавление отзыва пользователем ");
        return itemService.addComment(commentDto, itemId, userId);
    }
}
