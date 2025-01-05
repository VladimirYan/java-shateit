package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    void removeUser(Long userId);

    UserDto updateUser(UserDto userDto, Long userId);

    Collection<UserDto> getUsers();

    UserDto getUserById(Long userId);
}