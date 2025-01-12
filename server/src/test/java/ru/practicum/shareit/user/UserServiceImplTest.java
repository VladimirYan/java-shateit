package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private UserMapper userMapper;

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setName("newName");
        userDto.setEmail("a2@a2.com");

        UserDto dto = userService.createUser(userDto);

        assertNotNull(dto);
    }

    @Test
    void createUserValidEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("newName");
        userDto.setEmail("a1@a1.com");
        UserDto dto = userService.createUser(userDto);

        UserDto userDtoNew = new UserDto();
        userDtoNew.setName("newNameUser");
        userDtoNew.setEmail("a1@a1.com");

        UserAlreadyExistsException exp = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDtoNew));

        assertEquals("email=a1@a1.com уже используется", exp.getMessage());
    }

    @Test
    void createUserInvalidEmailNull() {
        UserDto userDto = new UserDto();
        userDto.setName("newName");
        userDto.setEmail(null);

        ValidationException exp = assertThrows(ValidationException.class, () -> userService.createUser(userDto));

        assertEquals("Отсутствует email пользователя", exp.getMessage());
    }

    @Test
    void removeUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("b@b.com");
        User savedUser = userRepository.save(user);

        userService.removeUser(savedUser.getId());
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("c@c.com");
        User savedUser = userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setName("newName");
        userDto.setEmail("d@d.com");
        UserDto updatedUser = userService.updateUser(userDto, savedUser.getId());
        assertEquals("newName", updatedUser.getName());
    }

    @Test
    void updateUserValidEmail() {
        User user = new User();
        user.setName("name");
        user.setEmail("c@c.com");
        User savedUser = userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setName("newName");
        userDto.setEmail("c@c.com");

        UserAlreadyExistsException exp = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userDto, savedUser.getId()));

        assertEquals("Такой email у пользователя уже существует", exp.getMessage());
    }

    @Test
    void getUsers() {
        Collection<UserDto> users = userService.getUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setName("name");
        user.setEmail("e@e.com");
        User savedUser = userRepository.save(user);

        UserDto users = userService.getUserById(savedUser.getId());
        assertNotNull(users);
    }

}
