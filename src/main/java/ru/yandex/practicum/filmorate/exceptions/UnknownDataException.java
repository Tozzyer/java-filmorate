package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownDataException extends RuntimeException {
    public UnknownDataException(String message) {
        super(message);
        log.warn("Ошибка запроса " + message);
    }
}