package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.jpa.CommentRepository;
import ru.practicum.shareit.item.repository.jpa.ItemRepository;
import ru.practicum.shareit.user.dto.dto.UserDto;
import ru.practicum.shareit.user.repository.jpa.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.item.ItemMapper.*;
import static ru.practicum.shareit.item.dto.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceDbImpl implements ItemService {

    private final JpaUserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        validateItemDto(itemDto);
        UserDto userFromDb = checkUserId(userId);
        log.debug("Создание элемента товара: {}; для пользователя {}", itemDto, userId);
        Item item = toItemDb(itemDto, toUser(userFromDb));
        Item savedItem = itemRepository.save(item);
        return toItemDto(savedItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        checkUserId(userId);
        log.debug("Обновление элемента товара: {}; для пользователя {}", itemDto, userId);

        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Товара с ID " + itemDto.getId() + " не найдено"));

        Item itemToUpdate = toItemUpdate(itemDto, existingItem);
        Item updatedItem = itemRepository.save(itemToUpdate);

        return toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Товара с ID " + itemId + " не найдено"));

        List<CommentDto> comments = getCommentsForItem(itemId);
        List<BookingDto> allOwnerBookings = getOwnerBookings(userId);
        List<BookingDto> bookingsForItem = allOwnerBookings.stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .collect(Collectors.toList());

        log.debug("Получение предмета с ID: {} для пользователя с ID: {}", itemId, userId);

        if (!bookingsForItem.isEmpty() && !comments.isEmpty()) {
            return toItemDtoWithBookingsAndComments(item, bookingsForItem, comments);
        } else if (!bookingsForItem.isEmpty()) {
            return toItemDtoWithBookings(item, bookingsForItem);
        } else if (!comments.isEmpty()) {
            return toItemDtoWithComments(item, comments);
        } else {
            return toItemDto(item);
        }
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        UserDto userFromDb = checkUserId(userId);

        List<Item> userItems = getUserItems(userFromDb.getId());
        List<CommentDto> commentsToUserItems = getCommentsForUserItems(userId);
        List<BookingDto> bookingsToUserItems = getOwnerBookings(userId);

        Map<Item, List<CommentDto>> itemsWithCommentsMap = mapItemsToComments(userItems, commentsToUserItems);
        Map<Item, List<BookingDto>> itemsWithBookingsMap = mapItemsToBookings(userItems, bookingsToUserItems);

        log.debug("Получение элементов пользователя с ID: {}", userFromDb.getId());

        return userItems.stream()
                .map(item -> toItemDtoWithBookingsAndComments(
                        item,
                        itemsWithBookingsMap.getOrDefault(item, Collections.emptyList()),
                        itemsWithCommentsMap.getOrDefault(item, Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text) {
        if (isTextEmpty(text)) {
            return Collections.emptyList();
        }
        log.debug("Поиск элементов по тексту: {}", text);
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Товара с ID " + itemId + " не найдено"));
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new EntityNotFoundException("Пользователь с ID " + ownerId + " не является владельцем товара с ID " + itemId);
        }
        return toItemDto(item);
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        validateCommentDto(commentDto);

        UserDto author = checkUserId(userId);
        verifyUserHasBooking(userId, itemId);

        ItemDto item = getItemById(itemId, userId);
        Comment comment = CommentMapper.toCommentDb(commentDto, toUser(author), toItem(item));

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Пустые поля в элементе ItemDto!");
        }
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new EmptyFieldException("Пустые поля в элементе ItemDto!");
        }
    }

    private UserDto checkUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователь с ID " + userId + " не существует");
        }
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + userId + " не найдено")));
    }

    private List<CommentDto> getCommentsForItem(long itemId) {
        return commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getOwnerBookings(long ownerId) {
        return bookingRepository.findAllByItem_Owner_Id(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Item> getUserItems(long userId) {
        return itemRepository.findByOwner_Id(userId, Sort.by(Sort.Direction.ASC, "id"));
    }

    private List<CommentDto> getCommentsForUserItems(long userId) {
        return commentRepository.findAllByItemsUserId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Map<Item, List<CommentDto>> mapItemsToComments(List<Item> items, List<CommentDto> comments) {
        Map<Item, List<CommentDto>> map = new HashMap<>();
        for (Item item : items) {
            List<CommentDto> itemComments = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());
            map.put(item, itemComments);
        }
        return map;
    }

    private Map<Item, List<BookingDto>> mapItemsToBookings(List<Item> items, List<BookingDto> bookings) {
        Map<Item, List<BookingDto>> map = new HashMap<>();
        for (Item item : items) {
            List<BookingDto> itemBookings = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());
            map.put(item, itemBookings);
        }
        return map;
    }

    private boolean isTextEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().trim().isEmpty()) {
            throw new IncorrectDataException("Текст комментария не может быть пустым!");
        }
    }

    private void verifyUserHasBooking(Long userId, Long itemId) {
        List<BookingDto> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(
                        userId, itemId, LocalDateTime.now())
                .stream()
                .map(BookingMapper::toBookingDto)
                .toList();
        if (bookings.isEmpty()) {
            throw new IncorrectDataException("Этот пользователь не имеет завершенных бронирований для данного товара.");
        }
    }
}
