package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (!StringUtils.hasText(user.getEmail())) {
            throw new ValidationException("Отсутствует email пользователя");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("email={} уже используется", user.getEmail());
            throw new UserAlreadyExistsException("email=" + user.getEmail() + " уже используется");
        }

        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public void removeUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail()) &&
                !userRepository.findById(userId).map(User::getEmail).orElse("").equals(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Такой email у пользователя уже существует");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updateUserFields(user, userDto);

        User updatedUser = userRepository.save(user);
        return userMapper.toUserDto(updatedUser);
    }

    private void updateUserFields(User user, UserDto userDto) {
        if (StringUtils.hasText(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (StringUtils.hasText(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return userMapper.toUserDto(user);
    }
}
