package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("name");
        dto.setEmail("a@a.com");
        when(userClient.createUser(dto)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": null, \"name\": \"name\", \"email\": \"a@a.com\"} ")
        ).andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("name");
        dto.setEmail("a@a.com");
        when(userClient.updateUser(0L, dto)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        this.mockMvc.perform(patch("/users/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": null, \"name\": \"name\", \"email\": \"email\"}")
        ).andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(userClient.getUserById(0L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        this.mockMvc.perform(get("/users/0")).andExpect(status().isOk());
    }

    @Test
    void removeUser() throws Exception {
        this.mockMvc.perform(delete("/users/0")).andExpect(status().isOk());
    }
}
