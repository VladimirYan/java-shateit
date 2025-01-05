package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper,
                              UserService userService,
                              ItemService itemService) {
        this.repository = bookingRepository;
        this.mapper = bookingMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {

        if (bookingInputDto.getItemId() == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        if (!itemService.getItemById(bookingInputDto.getItemId()).getAvailable()) {
            throw new ValidationException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования!");
        }

        Booking booking = mapper.toBooking(bookingInputDto, bookerId);

        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования самим владельцем!");
        }
        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));

        isGetEnd(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
                log.info("Пользователь с ID={} отменил бронирование с ID={}", userId, bookingId);
            } else {
                throw new NotFoundException("Подтвердить бронирование может только владелец вещи!");
            }
        } else if ((itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(booking.getItem().getId())) &&
                (!booking.getStatus().equals(Status.CANCELED)))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                throw new ValidationException("Решение по бронированию уже принято!");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(Status.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(Status.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }

        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getBooker().getId().equals(userId) || itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(booking.getItem().getId()))) {
            return mapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Посмотреть данные бронирования может только владелец вещи" +
                    " или бронирующий ее!");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId) {

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        bookings = switch (state) {
            case "ALL" -> repository.findByBookerId(userId, sortByStartDesc);
            case "CURRENT" -> repository.findActiveBookings(userId, LocalDateTime.now(),
                    LocalDateTime.now(), sortByStartDesc);
            case "PAST" -> repository.findPastBookings(userId, LocalDateTime.now(), sortByStartDesc);
            case "FUTURE" -> repository.findFutureBookings(userId, LocalDateTime.now(), sortByStartDesc);
            case "WAITING" -> repository.findByBookerAndStatus(userId, Status.WAITING, sortByStartDesc);
            case "REJECTED" -> repository.findByBookerAndStatus(userId, Status.REJECTED, sortByStartDesc);
            default -> throw new ValidationException("Unknown state: " + state);
        };
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Long userId) {

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        bookings = switch (state) {
            case "ALL" -> repository.findByOwner(userId, sortByStartDesc);
            case "CURRENT" -> repository.findOwnerActiveBookings(userId, LocalDateTime.now(),
                    LocalDateTime.now(), sortByStartDesc);
            case "PAST" -> repository.findOwnerPastBookings(userId, LocalDateTime.now(), sortByStartDesc);
            case "FUTURE" -> repository.findOwnerFutureBookings(userId, LocalDateTime.now(),
                    sortByStartDesc);
            case "WAITING" -> repository.findOwnerBookingsByStatus(userId, Status.WAITING, sortByStartDesc);
            case "REJECTED" -> repository.findOwnerBookingsByStatus(userId, Status.REJECTED, sortByStartDesc);
            default -> throw new ValidationException("Unknown state: " + state);
        };
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {

        return mapper.toBookingShortDto(repository.findLastBookingBeforeEnd(itemId,
                LocalDateTime.now()));
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {

        return mapper.toBookingShortDto(repository.findNextBookingAfterEnd(itemId,
                LocalDateTime.now()));
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {

        return repository.findLastBookingByUserAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }

    public void isGetEnd(Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже истекло!");
        }
    }
}
