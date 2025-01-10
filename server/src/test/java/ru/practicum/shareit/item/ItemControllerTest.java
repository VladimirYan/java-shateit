package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private ItemController controller;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    private CommentDto commentDto;
    private ItemDto itemDto;
    private UserDto owner;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text("TestCommentText")
                .authorName("TestAuthorName")
                .created(LocalDateTime.now())
                .build();

        owner = UserDto.builder()
                .id(1L)
                .name("Test")
                .email("Test@test.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("TestItemName")
                .description("TestItemDescription")
                .available(true)
                .owner(owner)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
    }

    @Test
    void addItem() throws Exception {
        when(itemService.add(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), UserDto.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking()), BookingShortDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking()), BookingShortDto.class))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments()), List.class));
    }

    @Test
    void getItemsByOwner() throws Exception {
        when(itemService.getItemsByOwner(anyLong())).thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/1")
                .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), UserDto.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking()), BookingShortDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking()), BookingShortDto.class))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments()), List.class));
    }

    @Test
    void getItemsBySearchQuery() throws Exception {
        when(itemService.getItemsBySearchQuery(anyString())).thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search?text=test")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.item", is(commentDto.getItem())))
                .andExpect(jsonPath("$.author", is(commentDto.getAuthor())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}
