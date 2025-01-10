package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookUpdateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {

    BookingDto bookItem(BookingInputDto bookingDto, Long bookerId);

    BookingDto update(Long bookingId, Long userId, BookUpdateRequestDto bookUpdateRequestDto);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookings(String state, Long userId);

    List<BookingDto> getBookingsOwner(String state, Long userId);

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);

    Booking getBookingWithUserBookedItem(Long itemId, Long userId);
}
