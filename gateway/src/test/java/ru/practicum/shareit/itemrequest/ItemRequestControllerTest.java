package ru.practicum.shareit.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.itemrequest.client.ItemRequestClient;
import ru.practicum.shareit.itemrequest.controller.ItemRequestController;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestClient.createItemRequest(new ItemRequestDto()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/requests")
                .header(USER_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new ItemRequestDto()))
        ).andExpect(status().isOk());
    }

    @Test
    void getUserRequests() throws Exception {
        when(itemRequestClient.getUserRequests(1L))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests")
                .header(USER_ID, 1)
        ).andExpect(status().isOk());
    }

    @Test
    void findByItemRequestId() throws Exception {
        when(itemRequestClient.findByItemRequestId(1L, 1L))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests/1")
                .header(USER_ID, 1)
        ).andExpect(status().isOk());
    }
}
