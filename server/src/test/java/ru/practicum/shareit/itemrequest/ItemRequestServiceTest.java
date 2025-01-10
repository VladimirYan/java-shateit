package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.RequestRepository;
import ru.practicum.shareit.itemrequest.service.RequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemRequestServiceTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItemRequest() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("desc");
        dto.setRequestorId(1L);
        dto.setCreated(LocalDateTime.now());
        requestService.createItemRequest(dto);
    }

    @Test
    void getUserRequests() {
        User user = User.builder()
                .name("Name")
                .email("a@a.com")
                .build();
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build();
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item = Item.builder()
                .name("name")
                .description("desc")
                .available(Boolean.TRUE)
                .owner(user)
                .requestId(savedItemRequest.getItemRequestId())
                .build();
        itemRepository.save(item);

        List<ItemRequestDto> result = requestService.getUserRequests(savedUser.getId());
        assertFalse(result.isEmpty());
    }

    @Test
    void findByItemRequestId() {
        User user = User.builder()
                .name("Name")
                .email("a@a.com")
                .build();
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build();
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item = Item.builder()
                .name("name")
                .description("desc")
                .available(Boolean.TRUE)
                .owner(user)
                .requestId(savedItemRequest.getItemRequestId())
                .build();
        itemRepository.save(item);

        ItemRequestDto result = requestService
                .findByItemRequestId(savedItemRequest.getItemRequestId(), savedUser.getId());
        assertNotNull(result);
    }
}
