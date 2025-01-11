package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookUpdateRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    void getBookings() throws Exception {
        when(bookingClient.getBookings(1, BookingState.ALL, 0, 10))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                .header(USER_ID, 1)
        ).andExpect(status().isOk());
    }

    @Test
    void bookItem() throws Exception {
        when(bookingClient.bookItem(1, new BookItemRequestDto()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(post("/bookings")
                .header(USER_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BookItemRequestDto()))
        ).andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingClient.getBooking(1, 1L))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/1")
                .header(USER_ID, 1)
        ).andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        BookUpdateRequestDto updateRequestDto = new BookUpdateRequestDto();
        updateRequestDto.setApproved(true);

        when(bookingClient.update(1, 1, updateRequestDto))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/bookings/1")
                .header(USER_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("approved", "true")
        ).andExpect(status().isOk());
    }

    @Test
    void getBookingsOwner() throws Exception {
        when(bookingClient.getBookingsOwner(
                1,
                Map.of("state", BookingState.ALL)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                .header(USER_ID, 1)
                .param("state", "ALL")
        ).andExpect(status().isOk());
    }
}