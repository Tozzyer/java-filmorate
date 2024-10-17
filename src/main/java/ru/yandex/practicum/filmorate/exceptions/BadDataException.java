package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadDataException extends RuntimeException {
    public BadDataException(String message) {
        super(message);
        log.warn("Ошибка ввода данных: " + message);
    }
}