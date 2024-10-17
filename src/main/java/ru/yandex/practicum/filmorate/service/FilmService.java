package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnknownDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {
    private InMemoryFilmStorage inMemoryFilmStorage;
    private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService (InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage){
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    //добавление лайка
    public Film addLike(Integer filmId, Integer id){
        if(!checkFilmUserAvalaibility(filmId,id)){
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно поставить лайк.");
        }
        log.info("Запрошено добавление лайка фильму: "+filmId+". От пользователя: "+id);
        return inMemoryFilmStorage.findAllFilms().stream()
                .filter(film -> film.getId()==filmId)
                .map(film -> film.addLike(id))
                .findFirst()
                .orElseThrow(()->new UnknownDataException("Запрошенные ресурсы отсутствуют"));
    }
    //удаление лайка
    public Film removeLike (Integer filmId, Integer id){
        if(!checkFilmUserAvalaibility(filmId,id)){
            throw new UnknownDataException("Запрошенные ресурсы отсутствуют. Невозможно удалить лайк.");
        }
        log.info("Запрошено удаление лайка фильму: "+filmId+". От пользователя: "+id);
        return inMemoryFilmStorage.findAllFilms().stream()
                .filter(film -> film.getId()==filmId)
                .map(film -> film.removeLike(id))
                .findFirst()
                .orElseThrow(()->new UnknownDataException("Запрошенные ресурсы отсутствуют"));
    }
    //топ10

    public Collection<Film> top(Integer count){
        log.info("Запрошен топ фильмов в количестве: "+count);
        return inMemoryFilmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getRating).reversed())
                .limit(count)
                .collect(Collectors.toList());

    }

    //проверка наличия
    //проверка добавления. Если всё хорошо, вернёт true
    private boolean checkFilmUserAvalaibility(Integer filmId, Integer id) {
        return inMemoryUserStorage.findAllUsers().stream().anyMatch(user -> user.getId()==id) &&
                inMemoryFilmStorage.findAllFilms().stream().anyMatch(film -> film.getId()==filmId);
    }
}
