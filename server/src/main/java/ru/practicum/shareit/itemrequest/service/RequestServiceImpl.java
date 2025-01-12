package ru.practicum.shareit.itemrequest.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.RequestRepository;
import ru.practicum.shareit.itemrequest.service.RequestService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemService itemService;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedItemRequest, null);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return requestRepository
                .findByRequestorId(userId)
                .stream()
                .map(itemRequest -> {
                    List<ItemDto> itemDtoList = itemService.getItemsByOwner(userId);
                    return itemRequestMapper.toItemRequestDto(itemRequest, itemDtoList);
                }).collect(toList());
    }

    @Override
    public ItemRequestDto findByItemRequestId(Long itemRequestId, Long userId) {
        ItemRequest itemRequest = requestRepository
                .findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Item request с id = " + itemRequestId + " не найден."));
        List<ItemDto> itemDtoList = itemService.getItemsByOwner(userId);
        return itemRequestMapper.toItemRequestDto(itemRequest, itemDtoList);
    }

}
