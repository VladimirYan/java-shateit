package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserMapper userMapper;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           ItemMapper itemMapper,
                           UserService userService,
                           BookingService bookingService,
                           UserMapper userMapper
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.bookingService = bookingService;
        this.userMapper = userMapper;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long ownerId) {

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        itemDto.setOwner(userMapper.toUserDto(user));

        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь  id=" + ownerId + " не найден");
        }

        itemDto.setId(itemId);

        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto, itemId)));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.getItemsByOwner(userRepository.findById(ownerId).orElseThrow())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toUpperCase();
        return itemRepository.findByNameIgnoreCaseAndAvailableTrue(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public void deleteItem(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()
                        -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (item.getOwner().getId().equals(ownerId)) {
            itemRepository.deleteById(itemId);
        } else {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
    }

    @Override
    public void deleteItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.getItemsByOwner(userRepository.findById(ownerId).orElseThrow());
        for (Item item : items) {
            itemRepository.deleteById(item.getId());
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        return itemMapper.toItemDto(item);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Comment comment = new Comment();
        Booking booking = bookingService.getBookingWithUserBookedItem(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
        Comment savedComment = commentRepository.save(comment);

        return itemMapper.toCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository
                .findAllByItemId(itemId)
                .stream()
                .map(itemMapper::toCommentDto)
                .collect(toList());
    }
}
