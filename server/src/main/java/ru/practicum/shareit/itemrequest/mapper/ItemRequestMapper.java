package ru.practicum.shareit.itemrequest.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;

import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestorId(),
                itemRequestDto.getCreated()
        );
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(
                itemRequest.getItemRequestId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getCreated(),
                items
        );
    }

}
