package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Новый пользователь создан");
        return user;
    }

    @Override
    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new UserValidationException("Пользователь отсутствует");
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён");
        return user;
    }

    private Integer getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }

    public void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserValidationException("Адрес электронной почты не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new UserValidationException("Неверный адрес электронной почты");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserValidationException("Дата рождения в будущем");
        }
    }

}