package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmValidationException extends RuntimeException {
    public FilmValidationException(String message) {
        super(message);
        log.warn("Ошибка валидации фильма: " + message);
    }
}