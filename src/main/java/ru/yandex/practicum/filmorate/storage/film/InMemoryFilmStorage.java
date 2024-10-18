package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadDataException;
import ru.yandex.practicum.filmorate.exceptions.UnknownDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Запрошены все фильмы");
        return films.values();
    }

    @Override
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Фильм создан");
        return film;
    }

    @Override
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new UnknownDataException("Фильм отсутствует в базе данных");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлён");
        return film;
    }

    private Integer getNextFilmId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return Math.toIntExact(++currentMaxId);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new BadDataException("Тело запроса не должно быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new BadDataException("Описание не может быть пустым");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new BadDataException("Имя не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new BadDataException("Число символов в описании не должно превышать 200");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadDataException("Дата выхода фильма не должна быть раньше дня рождения кинематорграфа");
        }
        if (film.getDuration() < 1) {
            throw new BadDataException("Неверная длительность фильма");
        }
    }

}
