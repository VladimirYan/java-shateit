package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.booking.dto.BookUpdateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.CustomUserNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void bookItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        BookingInputDto inputDto = new BookingInputDto();
        inputDto.setItemId(savedItem.getId());
        inputDto.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        inputDto.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));

        BookingDto dto = bookingService.bookItem(inputDto, savedUserBooker.getId());
        assertNotNull(dto);
    }

    @Test
    public void bookItem_ShouldThrowNotFoundException_WhenItemIdIsNull() {
        BookingInputDto bookingInputDto = new BookingInputDto();
        User booker = new User();
        bookingInputDto.setItemId(null);

        assertThrows(NotFoundException.class, () -> bookingService.bookItem(bookingInputDto, booker.getId()));
    }

    @Test
    public void bookItem_ShouldThrowValidationException_WhenItemIsNotAvailable() {
        User user = new User();
        user.setName("name");
        user.setEmail("a5@a5.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(false);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(savedItem.getId());
        bookingInputDto.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        bookingInputDto.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));

        assertThrows(ValidationException.class, () -> bookingService.bookItem(bookingInputDto, savedUser.getId()));
    }

    @Test
    public void bookItem_ShouldThrowNotFoundException_WhenBookerIsOwner() {

        User user = new User();
        user.setName("name");
        user.setEmail("a51@a5.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(savedItem.getId());
        bookingInputDto.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        bookingInputDto.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));

        assertThrows(NotFoundException.class, () -> bookingService.bookItem(bookingInputDto, savedUser.getId()));
    }

    @Test
    void update() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        BookUpdateRequestDto dto = new BookUpdateRequestDto();
        dto.setApproved(Boolean.TRUE);

        BookingDto shortDto = bookingService
                .update(savedBooking.getId(), savedUser.getId(), dto);
        assertNotNull(shortDto);
    }

    @Test
    void update_CancelBooking() {

        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.CANCELED);

        Booking savedBooking = bookingRepository.save(booking);

        BookUpdateRequestDto requestDto = new BookUpdateRequestDto();
        requestDto.setApproved(false);

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.update(booking.getId(), savedUser.getId(), requestDto));

        assertEquals("Бронирование было отменено!", exp.getMessage());
    }

    @Test
    public void update_UserNotFound() {

        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        BookUpdateRequestDto requestDto = new BookUpdateRequestDto();
        requestDto.setApproved(false);

        Long invalidUserId = 999L; // Несуществующий пользователь

        CustomUserNotFoundException exception = assertThrows(CustomUserNotFoundException.class, () -> {
            bookingService.update(savedBooking.getId(), invalidUserId, requestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    public void update_BookingNotFound() {
        BookUpdateRequestDto requestDto = new BookUpdateRequestDto();
        requestDto.setApproved(false);

        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Long invalidBookingId = 999L; // Несуществующее бронирование

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.update(invalidBookingId, savedUser.getId(), requestDto);
        });

        assertEquals("Бронирование с ID=999 не найдено!", exception.getMessage());
    }

    @Test
    void getBookingById() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        BookingDto dto = bookingService.getBookingById(savedBooking.getId(), savedUser.getId());
        assertNotNull(dto);
    }

    @Test
    void getBookings() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> dtoList = bookingService
                .getBookings("ALL", savedUserBooker.getId());
        assertFalse(dtoList.isEmpty());
    }

    @Test
    void getBookings_shouldThrowValidationException_whenStateIsUnknown() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        assertThrows(ValidationException.class, () -> {
            bookingService.getBookings("UNKNOWN_STATE", savedUser.getId());
        });
    }

    @Test
    void getBookings_shouldReturnRejectedBookings_whenStateIsRejected() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        List<BookingDto> bookings = bookingService.getBookings("REJECTED", savedUser.getId());
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookings_shouldReturnWaitingBookings_whenStateIsWaiting() {
        User user = new User();
        user.setName("name");
        user.setEmail("a52@a.com");
        User savedUser = userRepository.save(user);

        List<BookingDto> bookings = bookingService.getBookings("WAITING", savedUser.getId());
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookingsOwner() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> dtoList = bookingService
                .getBookingsOwner("ALL", savedUser.getId());
        assertFalse(dtoList.isEmpty());
    }

    @Test
    void getBookingsOwner_shouldThrowValidationException_whenStateIsUnknown() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsOwner("UNKNOWN_STATE", savedUser.getId());
        });
    }

    @Test
    void getBookingsOwner_shouldReturnRejectedBookings_whenStateIsRejected() {
        User user = new User();
        user.setName("name");
        user.setEmail("a87@a.com");
        User savedUser = userRepository.save(user);

        List<BookingDto> bookings = bookingService.getBookingsOwner("REJECTED", savedUser.getId());
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnRejectedBookings_whenStateIsWaiting() {
        User user = new User();
        user.setName("name");
        user.setEmail("a88@a.com");
        User savedUser = userRepository.save(user);

        List<BookingDto> bookings = bookingService.getBookingsOwner("WAITING", savedUser.getId());
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnActiveBookings_whenStateIsCURRENT() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookingsOwner("CURRENT", savedUser.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnPastBookings_whenStateIsPAST() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookingsOwner("PAST", savedUser.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnFUTUREBookings_whenStateIsFUTURE() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2025-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookingsOwner("FUTURE", savedUser.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookings_shouldReturnPastBookings_whenStateIsPAST() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookings("PAST", savedUserBooker.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnWAITINGBookings_whenStateIsWAITING() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookingsOwner("WAITING", savedUser.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsOwner_shouldReturnREJECTEDBookings_whenStateIsREJECTED() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.REJECTED);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookingsOwner("REJECTED", savedUser.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookings_shouldReturnFUTUREBookings_whenStateIsFUTURE() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2025-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookings("FUTURE", savedUserBooker.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookings_shouldReturnActiveBookings_whenStateIsCURRENT() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService
                .getBookings("CURRENT", userBooker.getId());
        assertEquals(1, bookings.size());
    }

    @Test
    void getLastBooking() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        BookingShortDto dto = bookingService.getLastBooking(savedItem.getId());
        assertNotNull(dto);
    }

    @Test
    void getNextBooking() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2025-08-04T10:11:30"));
        booking.setEnd(LocalDateTime.parse("2025-08-05T10:11:30"));
        booking.setItem(savedItem);
        booking.setBooker(savedUserBooker);
        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        BookingShortDto dto = bookingService.getNextBooking(savedItem.getId());
        assertNotNull(dto);
    }

    @Test
    void getBookingWithUserBookedItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        User userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("b@b.com");
        User savedUserBooker = userRepository.save(userBooker);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking bookingToSave = new Booking();
        bookingToSave.setStart(LocalDateTime.parse("2015-08-04T10:11:30"));
        bookingToSave.setEnd(LocalDateTime.parse("2015-08-05T10:11:30"));
        bookingToSave.setItem(savedItem);
        bookingToSave.setBooker(savedUserBooker);
        bookingToSave.setStatus(Status.APPROVED);

        bookingRepository.save(bookingToSave);

        Booking booking = bookingService
                .getBookingWithUserBookedItem(savedItem.getId(), savedUserBooker.getId());
        assertNotNull(booking);
    }

}
