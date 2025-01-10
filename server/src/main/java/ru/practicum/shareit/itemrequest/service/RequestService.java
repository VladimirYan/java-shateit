package ru.practicum.shareit.itemrequest.service;

import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(Long userId);

    ItemRequestDto findByItemRequestId(Long itemRequestId, Long userId);

}
