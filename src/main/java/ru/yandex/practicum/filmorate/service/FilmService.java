package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@Service
@Slf4j
public class FilmService {
    private final FilmDbStorage filmStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }


    public Film removeLike(Integer filmId, Integer id) {
        return filmStorage.removeLike(filmId, id);
    }

    public Collection<Film> top(Integer count) {
        return filmStorage.top(count);
    }

    public Film addLike(Integer filmId, Integer id) {
        return filmStorage.addLike(filmId, id);
    }

    public Collection<Genre> getAllGenres() {
        log.info("Сработал запрос жанров в сервисе");
        return filmStorage.getAllGenres();
    }

    public Genre getGenre(Integer count) {
        return filmStorage.getGenre(count);
    }

    public Collection<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpa(Integer id) {
        return filmStorage.getMpa(id);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }
}
