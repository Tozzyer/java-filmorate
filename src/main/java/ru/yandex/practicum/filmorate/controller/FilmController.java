package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new FilmValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new FilmValidationException("Число символов в описании не должно превышать 200");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            throw new FilmValidationException("Дата выхода фильма не должна быть раньше дня рождения кинематорграфа");
        }
        if (film.getDuration() < 1) {
            throw new FilmValidationException("Неверная длительность фильма");
        }
        // формируем дополнительные данные
        film.setId(getNextFilmId());

        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new FilmValidationException("Описание не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new FilmValidationException("Число символов в описании не должно превышать 200");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            throw new FilmValidationException("Дата выхода фильма не должна быть раньше дня рождения кинематорграфа");
        }
        if (film.getDuration() < 1) {
            throw new FilmValidationException("Неверная длительность фильма");
        }
        if (!films.containsKey(film.getId())) {
            throw new FilmValidationException("Фильм отсутствует в базе данных");
        }
        films.put(film.getId(), film);
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


}
