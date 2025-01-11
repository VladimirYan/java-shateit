package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto); // добавление пользователя

    void removeUser(Long userId); // удаление пользователя

    UserDto updateUser(UserDto userDto, Long userId); //модификация, обновление пользователя

    Collection<UserDto> getUsers(); // Получение всех пользователей

    UserDto getUserById(Long userId); // Получение пользователя по id
}
