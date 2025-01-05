package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.ArrayList;


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

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Отсутствует email пользователя");
        }

        if (userRepository.findAll().stream()
                .anyMatch(user2 -> user2.getEmail().equals(user.getEmail()))) {
            log.error("email={} уже используется", user.getEmail());
            throw new UserAlreadyExistsException("email=" + user.getEmail() + " уже используется");
        }

        User user1 = userRepository.save(user);

        return userMapper.toUserDto(user1);
    }

    @Override
    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {

        if (userRepository.existsByEmail(userDto.getEmail()))
            throw new UserAlreadyExistsException("Такой email у пользователя уже существует");

        if (userDto.getId() == null) {
            userDto.setId(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return userMapper.toUserDto(user);
    }
}
