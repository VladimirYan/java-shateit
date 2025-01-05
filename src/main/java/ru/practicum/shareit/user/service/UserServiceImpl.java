package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inmemory.UserRepository;


import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new EmptyFieldException("Электронная почта отсутствует");
        }
        log.debug("Создание пользователя: {}", userDto);

        User user = toUser(userDto);
        User createdUser = userRepository.create(user);

        return toUserDto(createdUser);
    }

    @Override
    public UserDto getById(long id) {
        validateUserExists(id);
        log.debug("Получение пользователя по ID: {}", id);

        User user = userRepository.getById(id);
        return toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.debug("Получение всех пользователей");

        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        validateUserExists(userDto.getId());
        log.debug("Обновление пользователя: {}", userDto);

        User existingUser = userRepository.getById(userDto.getId());
        User updatedUser = toUserUpdate(userDto, existingUser);
        User savedUser = userRepository.update(updatedUser);

        return toUserDto(savedUser);
    }

    @Override
    public void delete(long id) {
        validateUserExists(id);
        log.debug("Удаление пользователя по ID: {}", id);

        userRepository.delete(id);
    }

    private void validateUserExists(long id) {
        if (userRepository.getById(id) == null) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
    }
}

