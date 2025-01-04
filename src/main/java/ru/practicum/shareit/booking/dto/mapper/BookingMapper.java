package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.dto.BookingLiteDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.dto.BookingDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.user.dto.dto.UserDto;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.BookingDtoBuilder bookingBuilder = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus());

        ItemDto itemDto = toItemDtoIfPresent(booking.getItem());
        if (itemDto != null) {
            bookingBuilder.item(itemDto)
                    .itemId(itemDto.getId());
        }

        UserDto bookerDto = toUserDtoIfPresent(booking.getBooker());
        if (bookerDto != null) {
            bookingBuilder.booker(bookerDto);
        }

        return bookingBuilder.build();
    }

    public static Booking toBookingDb(BookingDto bookingDto, Item item, User booker) {
        long bookingId = bookingDto.getId() != null ? bookingDto.getId() : 0L;
        return Booking.builder()
                .id(bookingId)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .item(item)
                .booker(booker)
                .build();
    }

    public static Booking toBookingUpdate(BookingDto bookingDto, Booking booking) {
        Booking.BookingBuilder bookingBuilder = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(bookingDto.getStatus() != null ? bookingDto.getStatus() : booking.getStatus());

        if (bookingDto.getItem() != null) {
            Item updatedItem = toItem(bookingDto.getItem());
            bookingBuilder.item(updatedItem);
        }

        if (bookingDto.getBooker() != null) {
            User updatedBooker = toUser(bookingDto.getBooker());
            bookingBuilder.booker(updatedBooker);
        }

        return bookingBuilder.build();
    }

    public static BookingLiteDto toBookingLiteDto(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }

        BookingLiteDto.BookingLiteDtoBuilder liteBuilder = BookingLiteDto.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus());

        ItemDto itemDto = bookingDto.getItem();
        if (itemDto != null) {
            liteBuilder.item(itemDto);
        }

        UserDto booker = bookingDto.getBooker();
        if (booker != null) {
            liteBuilder.bookerId(booker.getId());
        }

        return liteBuilder.build();
    }

    private static ItemDto toItemDtoIfPresent(Item item) {
        return item != null ? toItemDto(item) : null;
    }

    private static UserDto toUserDtoIfPresent(User user) {
        return user != null ? toUserDto(user) : null;
    }

    private static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .build();
    }

    private static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    private static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
