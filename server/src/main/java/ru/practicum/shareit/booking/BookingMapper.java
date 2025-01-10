package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class BookingMapper {
    private final UserService userService;
    private final ItemService itemService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserService userService, ItemService itemService,
                         UserMapper userMapper, ItemMapper itemMapper) {
        this.userService = userService;
        this.itemService = itemService;
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getStatus(),
                    userMapper.toUserDto(booking.getBooker()),
                    itemMapper.toItemDto(booking.getItem())
            );
        } else {
            return null;
        }
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        if (booking != null) {
            return new BookingShortDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getBooker().getId()
            );
        } else {
            return null;
        }
    }

    public Booking toBooking(BookingInputDto bookingInputDto, Long bookerId) {
        return new Booking(
                null,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                Status.WAITING,
                userMapper.toUser(userService.getUserById(bookerId)),
                itemMapper.toItem(itemService.getItemById(bookingInputDto.getItemId()), bookerId)
        );
    }
}
