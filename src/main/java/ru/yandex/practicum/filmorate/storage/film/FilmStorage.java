package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> findAllFilms();

    public Film createFilm(@RequestBody Film film);

    public Film updateFilm(@RequestBody Film film);

}
