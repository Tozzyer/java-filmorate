package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private InMemoryFilmStorage inMemoryFilmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage=inMemoryFilmStorage;
        this.filmService=filmService;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    //добавление лайка
    @PutMapping("{filmId}/like/{id}")
    public Film addLike(@PathVariable Integer filmId, @PathVariable Integer id){
        return filmService.addLike(filmId, id);
    }

    //удаление лайка
    @DeleteMapping("{filmId}/like/{id}")
    public Film removeLike(@PathVariable Integer filmId, @PathVariable Integer id){
        return filmService.removeLike(filmId, id);
    }
    //топ10

    @GetMapping("/popular")
    public Collection<Film> getTop (@RequestParam(defaultValue = "10") Integer count){
        return filmService.top(count);
    }
}
