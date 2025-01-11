package ru.practicum.shareit.itemrequest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.service.RequestService;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto) {
        return requestService.createItemRequest(itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByItemRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long requestId) {
        return requestService.findByItemRequestId(requestId, userId);
    }

}
