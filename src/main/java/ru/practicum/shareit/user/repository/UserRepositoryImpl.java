package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmails = new HashSet<>();
    private long currentUserId = 0;

    @Override
    public User create(User user) {
        log.debug(CREATE_USER_LOG, user);
        user.setId(generateUserId());

        if (!uniqueEmails.add(user.getEmail())) {
            currentUserId--;
            throw new EmailIsAlreadyRegisteredException(USER_EXISTS_ERROR);
        }

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getById(long id) {
        log.debug(GET_USER_BY_ID_LOG, id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        log.debug(GET_ALL_USERS_LOG);
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new NoSuchElementException("Пользователь с таким ID не найден!");
        }

        String newEmail = user.getEmail();
        String oldEmail = existingUser.getEmail();

        if (!newEmail.equals(oldEmail)) {
            if (uniqueEmails.add(newEmail)) {
                uniqueEmails.remove(oldEmail);
            } else {
                throw new EmailIsAlreadyRegisteredException(USER_EXISTS_ERROR);
            }
        }

        log.debug(UPDATE_USER_LOG, user.getId(), user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            uniqueEmails.remove(removedUser.getEmail());
            log.debug(DELETE_USER_LOG, id);
        } else {
            log.warn("Попытка удалить несуществующего пользователя с ID: {}", id);
        }
    }

    private synchronized long generateUserId() {
        return ++currentUserId;
    }

    private static final String CREATE_USER_LOG = "Создание пользователя: {}";
    private static final String USER_EXISTS_ERROR = "Пользователь с этим email уже существует!";
    private static final String GET_USER_BY_ID_LOG = "Получение пользователя по ID: {}";
    private static final String GET_ALL_USERS_LOG = "Получение всех пользователей";
    private static final String UPDATE_USER_LOG = "Обновление пользователя с ID: {}, данные: {}";
    private static final String DELETE_USER_LOG = "Удаление пользователя с ID: {}";
}
