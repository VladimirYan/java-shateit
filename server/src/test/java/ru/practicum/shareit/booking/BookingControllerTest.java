package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookUpdateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private BookingController controller;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    private BookingDto bookingDto;
    private BookingInputDto bookinginputDto;
    private BookingShortDto bookingShortDto;
    private BookUpdateRequestDto bookUpdateRequestDto;
    private ItemDto itemDto;
    private UserDto booker;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        booker = UserDto.builder()
                .id(1L)
                .name("Test")
                .email("Test@test.com")
                .build();
        when(userService.createUser(booker)).thenReturn(booker);

        itemDto = ItemDto.builder()
                .id(1L)
                .name("TestItemName")
                .description("TestItemDescription")
                .available(true)
                .owner(booker)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .booker(booker)
                .item(itemDto)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(booker.getId())
                .build();

        bookUpdateRequestDto = BookUpdateRequestDto.builder()
                .approved(false)
                .build();

        bookinginputDto = BookingInputDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(any(), anyLong())).thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void bookItem() throws Exception {
        when(bookingService.bookItem(new BookingInputDto(), 1L))
                .thenReturn(new BookingDto());
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BookingDto()))
        ).andExpect(status().isOk());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void update() throws Exception {
        when(bookingService.update(1L, 1L, new BookUpdateRequestDto()))
                .thenReturn(new BookingDto());

        mvc.perform(patch("/bookings/1")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BookUpdateRequestDto()))
        ).andExpect(status().isOk());
    }

    @Test
    void getBookingsOwner() throws Exception {
        when(bookingService.getBookingsOwner(anyString(), anyLong())).thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
