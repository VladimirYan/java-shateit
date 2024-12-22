package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        validateUserDto(userDto);
        log.debug(CREATE_USER_LOG, userDto);
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.create(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto getById(long id) {
        validateId(id);
        log.debug(GET_USER_BY_ID_LOG, id);
        User user = userRepository.getById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.debug(GET_ALL_USERS_LOG);
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        validateId(userDto.getId());
        log.debug(UPDATE_USER_LOG, userDto);
        User existingUser = userRepository.getById(userDto.getId());
        User updatedUser = userRepository.update(UserMapper.toUserUpdate(userDto, existingUser));
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(long id) {
        validateId(id);
        log.debug(DELETE_USER_LOG, id);
        userRepository.delete(id);
    }

    private void validateId(long id) {
        if (userRepository.getById(id) == null) {
            log.warn("Пользователь с ID {} не найден", id);
            throw new EntityNotFoundException(String.format("Пользователь с ID %d не найден", id));
        }
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            log.warn("Попытка создать пользователя с пустым Email");
            throw new EmptyFieldException(EMPTY_EMAIL_ERROR);
        }
    }

    private static final String CREATE_USER_LOG = "Создание пользователя: {}";
    private static final String EMPTY_EMAIL_ERROR = "Поле Email пустое";
    private static final String GET_USER_BY_ID_LOG = "Получение пользователя по ID: {}";
    private static final String GET_ALL_USERS_LOG = "Получение всех пользователей";
    private static final String UPDATE_USER_LOG = "Обновление пользователя: {}";
    private static final String DELETE_USER_LOG = "Удаление пользователя по ID: {}";
}
