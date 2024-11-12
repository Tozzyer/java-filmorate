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
        log.info("Команда: найти все фильмы");
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        log.info("Команда: создать фильм " + film.getName());
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Команда: обновить фильм " + film.getName());
        return filmStorage.updateFilm(film);
    }


    public Film removeLike(Integer filmId, Integer id) {
        log.info("Команда: удалить лайк");
        return filmStorage.removeLike(filmId, id);
    }

    public Collection<Film> top(Integer count) {
        log.info("Команда: получить топ фильмов (" + count + ")");
        return filmStorage.top(count);
    }

    public Film addLike(Integer filmId, Integer id) {
        log.info("Команда: добавить лайк");
        return filmStorage.addLike(filmId, id);
    }

    public Collection<Genre> getAllGenres() {
        log.info("Команда: получить все жанры");
        return filmStorage.getAllGenres();
    }

    public Genre getGenre(Integer count) {
        log.info("Команда: получить жанр");
        return filmStorage.getGenre(count);
    }

    public Collection<Mpa> getAllMpa() {
        log.info("Команда: получить все возрастные рейтинги");
        return filmStorage.getAllMpa();
    }

    public Mpa getMpa(Integer id) {
        log.info("Команда: получить возрастной рейтинг");
        return filmStorage.getMpa(id);
    }

    public Film getFilm(Integer id) {
        log.info("Команда: запрос фильма");
        return filmStorage.getFilm(id);
    }
}
