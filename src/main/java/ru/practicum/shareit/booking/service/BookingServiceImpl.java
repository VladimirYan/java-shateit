package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingAccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.jpa.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.jpa.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final JpaUserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        UserDto userFromDb = checkingUserId(bookerId);

        Item itemFromDb = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Предмет с ID " + bookingDto.getItemId() + " не найден"));

        long ownerId = itemFromDb.getOwner().getId();
        if (ownerId == bookerId) {
            throw new EntityNotFoundException("Владелец не может бронировать свой предмет");
        }

        if (!itemFromDb.getAvailable()) {
            throw new IncorrectDataException("Бронирование: Предмет недоступен");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Бронирование: Даты отсутствуют");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isEndBeforeStart = bookingDto.getEnd().isBefore(bookingDto.getStart());
        boolean isStartEqualsEnd = bookingDto.getStart().isEqual(bookingDto.getEnd());
        boolean isEndBeforeNow = bookingDto.getEnd().isBefore(now);
        boolean isStartBeforeNow = bookingDto.getStart().isBefore(now);

        if (isEndBeforeStart || isStartEqualsEnd || isEndBeforeNow || isStartBeforeNow) {
            throw new IncorrectDataException("Бронирование: Некорректные даты");
        }

        bookingDto.setStatus(BookingStatus.WAITING);

        Booking booking = toBookingDb(bookingDto, itemFromDb, toUser(userFromDb));
        Booking savedBooking = bookingRepository.save(booking);
        return toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, String approve) {
        checkingUserId(ownerId);

        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Бронирование с ID " + bookingId + " не найдено"));
        BookingDto bookingDto = toBookingDto(existingBooking);

        Long bookingOwnerId = bookingDto.getItem().getOwnerId();
        if (!bookingOwnerId.equals(ownerId)) {
            throw new BookingAccessDeniedException(
                    "Пользователь с ID " + ownerId + " не является владельцем предмета");
        }

        String approveLower = approve.toLowerCase();
        switch (approveLower) {
            case "true":
                if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new IncorrectDataException("Статус уже подтверждён");
                }
                bookingDto.setStatus(BookingStatus.APPROVED);
                break;
            case "false":
                bookingDto.setStatus(BookingStatus.REJECTED);
                break;
            default:
                throw new IncorrectDataException("Некорректное значение approve: " + approve);
        }

        Booking updatedBooking = toBookingUpdate(bookingDto, existingBooking);
        bookingRepository.save(updatedBooking);

        return toBookingDto(updatedBooking);
    }


    @Override
    public BookingDto getBookingInfo(Long bookingId, Long userId) {
        checkingUserId(userId);

        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Бронирование с ID " + bookingId + " не найдено"));
        BookingDto bookingDto = toBookingDto(existingBooking);

        Long ownerId = bookingDto.getItem().getOwnerId();
        Long bookerId = bookingDto.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new EntityNotFoundException(
                    "Пользователь с ID " + userId + " не является владельцем или создателем бронирования");
        }

        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        checkingUserId(userId);

        checkingBookingState(state);

        String stateUpper = state.toUpperCase();

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        bookings = switch (stateUpper) {
            case "WAITING" -> bookingRepository.findAllByBookerIdAndWaitingStatus(
                    userId, BookingStatus.WAITING, SORT_BY_START_DESC);
            case "REJECTED" -> bookingRepository.findAllByBookerIdAndRejectedStatus(
                    userId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndCurrentStatus(
                    userId, now, SORT_BY_START_DESC);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndFutureStatus(
                    userId, now, SORT_BY_START_DESC);
            case "PAST" -> bookingRepository.findAllByBookerIdAndPastStatus(
                    userId, now, SORT_BY_START_DESC);
            case "ALL" -> bookingRepository.findAllByBooker_Id(userId, SORT_BY_START_DESC);
            default -> new ArrayList<>();
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, String state) {
        checkingUserId(ownerId);

        checkingBookingState(state);

        List<Long> ownerItemIds = itemRepository.findByOwner_Id(ownerId, Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        if (ownerItemIds.isEmpty()) {
            throw new IncorrectDataException("Этот метод предназначен только для пользователей, у которых есть предметы");
        }

        List<Booking> bookings;
        String stateUpper = state.toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        bookings = switch (stateUpper) {
            case "WAITING" -> bookingRepository.findAllByOwnerItemsAndWaitingStatus(
                    ownerItemIds, BookingStatus.WAITING, SORT_BY_START_DESC);
            case "REJECTED" -> bookingRepository.findAllByOwnerItemsAndRejectedStatus(
                    ownerItemIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC);
            case "CURRENT" -> bookingRepository.findAllByOwnerItemsAndCurrentStatus(
                    ownerItemIds, now, SORT_BY_START_DESC);
            case "FUTURE" -> bookingRepository.findAllByOwnerItemsAndFutureStatus(
                    ownerItemIds, now, SORT_BY_START_DESC);
            case "PAST" -> bookingRepository.findAllByOwnerItemsAndPastStatus(
                    ownerItemIds, now, SORT_BY_START_DESC);
            case "ALL" -> bookingRepository.findAllByOwnerItems(
                    ownerItemIds, SORT_BY_START_DESC);
            default -> new ArrayList<>();
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private UserDto checkingUserId(Long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Отсутствует пользователь с ID в заголовке: " + userId);
        }
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new BookingAccessDeniedException("Пользователь с ID " + userId + " не найден")));
    }

    private void checkingBookingState(String state) {
        try {
            BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Не поддерживаемый статус бронирования: " + state);
        }
    }
}
