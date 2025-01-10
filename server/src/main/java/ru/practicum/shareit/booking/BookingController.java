package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookUpdateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.CustomUserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService, UserRepository userRepository) {
        this.service = bookingService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение " +
                "списка всех бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        return service.getBookings(state, userId);
    }

    @ResponseBody
    @PostMapping
    public BookingDto bookItem(@RequestBody BookingInputDto bookingInputDto,
                               @RequestHeader(USER_ID) Long bookerId) {
        existUser(bookerId);
        log.info("Получен запрос на создание бронирования от пользователя с ID={}", bookerId);
        return service.bookItem(bookingInputDto, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
        return service.getBookingById(bookingId, userId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId,
                             @RequestBody BookUpdateRequestDto bookUpdateRequestDto) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
        return service.update(bookingId, userId, bookUpdateRequestDto);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        return service.getBookingsOwner(state, userId);
    }

    private void existUser(@RequestHeader(USER_ID) Long userId) {
        if (!userRepository.existsById(userId) || userId == null) {
            throw new CustomUserNotFoundException("Пользователь не найден");
        }
    }
}
