package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class ItemMapper {

    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserMapper userMapper;

    public ItemMapper(UserService userService,
                      BookingService bookingService,
                      ItemService itemService,
                      UserMapper userMapper
    ) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.itemService = itemService;
        this.userMapper = userMapper;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? userMapper.toUserDto(item.getOwner()) : null,
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                itemService.getCommentsByItemId(item.getId())
        );
    }

    public Item toItem(ItemDto itemDto, Long owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner() != null ?  userMapper.toUser(itemDto.getOwner()) : null,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                null,
                userMapper.toUserDto(comment.getAuthor()),
                comment.getCreated());
    }
}
