package ru.practicum.shareit.itemrequest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.client.ItemRequestClient;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestClient.createItemRequest(itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByItemRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long requestId) {
        return itemRequestClient.findByItemRequestId(requestId, userId);
    }

}
