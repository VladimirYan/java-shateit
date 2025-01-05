package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.jpa.JpaUserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceDbImpl implements UserService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        validateUserDto(userDto);
        log.debug("Создание пользователя: {}", userDto);
        User user = toUser(userDto);
        User savedUser = userRepository.save(user);
        return toUserDto(savedUser);
    }

    @Override
    public UserDto getById(long id) {
        log.debug("Получение пользователя по ID: {}", id);
        User user = findUserById(id);
        return toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.debug("Обновление пользователя: {}", userDto);
        User existingUser = findUserById(userDto.getId());
        User userToUpdate = toUserUpdate(userDto, existingUser);
        User updatedUser = userRepository.save(userToUpdate);
        return toUserDto(updatedUser);
    }

    @Override
    public void delete(long id) {
        User user = findUserById(id);
        log.debug("Удаление пользователя по ID: {}", id);
        userRepository.deleteById(user.getId());
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            log.error("Попытка создать пользователя без email");
            throw new EmptyFieldException("Email не заполнен");
        }
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Пользователь с ID " + id + " не найден";
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
    }


    private User toUser(UserDto userDto) {
        return UserMapper.toUser(userDto);
    }

    private User toUserUpdate(UserDto userDto, User existingUser) {
        return UserMapper.toUserUpdate(userDto, existingUser);
    }
}
