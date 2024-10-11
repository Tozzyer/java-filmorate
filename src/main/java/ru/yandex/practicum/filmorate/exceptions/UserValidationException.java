package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
        log.warn("Ошибка валидации пользователя: " + message);
    }
}