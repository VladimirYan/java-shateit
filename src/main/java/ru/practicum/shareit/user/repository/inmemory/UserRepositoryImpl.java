package ru.practicum.shareit.user.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmails = new HashSet<>();
    private long userIdCounter = 0;

    @Override
    public User create(User user) {
        log.debug("Создание пользователя: {}", user);
        if (!uniqueEmails.add(user.getEmail())) {
            throw new EmailIsAlreadyRegisteredException("Пользователь с этим email уже зарегистрирован!");
        }

        user.setId(++userIdCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(long id) {
        log.debug("Получение пользователя по ID: {}", id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        log.debug("Получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new EntityNotFoundException("Пользователь не найден с ID: " + user.getId());
        }

        String newEmail = user.getEmail();
        String oldEmail = existingUser.getEmail();

        if (!newEmail.equals(oldEmail)) {
            if (!uniqueEmails.contains(newEmail)) {
                uniqueEmails.remove(oldEmail);
                uniqueEmails.add(newEmail);
            } else {
                throw new EmailIsAlreadyRegisteredException("Пользователь с этим email уже зарегистрирован!");
            }
        }

        log.debug("Обновление пользователя с ID: {}, данные: {}", user.getId(), user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            uniqueEmails.remove(removedUser.getEmail());
            log.debug("Удаление пользователя с ID: {}", id);
        } else {
            log.warn("Попытка удалить несуществующего пользователя с ID: {}", id);
            throw new EntityNotFoundException("Пользователь не найден с ID: " + id);
        }
    }
}
