package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void correctFilm() {
        FilmController master = new FilmController();
        Film film = new Film(1, "The Hateful eight", "Western", LocalDate.of(2015, 12, 18), 187);
        master.createFilm(film);
        Collection<Film> filmsCollect = master.findAllFilms();
        ArrayList<Film> testFilms = new ArrayList<>(filmsCollect);
        Assertions.assertEquals(film, testFilms.get(0));
    }

    @Test
    void incorretDateFilm() {
        FilmController master = new FilmController();
        Film film = new Film(0, "The Hateful eight", "Western", LocalDate.of(1890, 12, 18), 187);
        FilmValidationException thrown = assertThrows(
                FilmValidationException.class,
                () -> master.createFilm(film),
                "Дата выхода фильма не должна быть раньше дня рождения кинематорграфа"
        );

    }

    @Test
    void incorretFilmNaming() {
        FilmController master = new FilmController();
        Film film = new Film(0, " ", "Western", LocalDate.of(2015, 12, 18), 187);
        FilmValidationException thrown = assertThrows(
                FilmValidationException.class,
                () -> master.createFilm(film),
                "Название не может быть пустым"
        );
    }

    @Test
    void incorretFilmDuration() {
        FilmController master = new FilmController();
        Film film = new Film(0, "The Hateful Eight", "Western", LocalDate.of(2015, 12, 18), -187);
        FilmValidationException thrown = assertThrows(
                FilmValidationException.class,
                () -> master.createFilm(film),
                "Неверная длительность фильма"
        );
    }

    @Test
    void correctUserTest() {
        UserController master = new UserController();
        User user = new User(0, "JD@post.com", "JohnDoe", "John", LocalDate.of(1980, 12, 12));
        master.createUser(user);
        Collection<User> usersCollect = master.findAllUsers();
        ArrayList<User> testFilms = new ArrayList<>(usersCollect);
        Assertions.assertEquals(user, testFilms.get(0));
    }

    @Test
    void incorrectMailUserTest() {
        UserController master = new UserController();
        User user = new User(0, "JDpost.com", "JohnDoe", "John", LocalDate.of(1980, 12, 12));
        UserValidationException thrown = assertThrows(
                UserValidationException.class,
                () -> master.createUser(user),
                "Неверный адрес электронной почты"
        );
    }

    @Test
    void incorrectEmptyMailUserTest() {
        UserController master = new UserController();
        User user = new User(0, " ", "JohnDoe", "John", LocalDate.of(1980, 12, 12));
        UserValidationException thrown = assertThrows(
                UserValidationException.class,
                () -> master.createUser(user),
                "Адрес электронной почты не может быть пустым"
        );
    }

    @Test
    void incorrectLoginUserTest() {
        UserController master = new UserController();
        User user = new User(0, "JD@post.com", "John Doe", "John", LocalDate.of(1980, 12, 12));
        UserValidationException thrown = assertThrows(
                UserValidationException.class,
                () -> master.createUser(user),
                "Логин не может быть пустым или содержать пробелы"
        );
    }

    @Test
    void incorrectNameUserTest() {
        UserController master = new UserController();
        User user = new User(0, "JD@post.com", "JohnDoe", "", LocalDate.of(1980, 12, 12));
        master.createUser(user);
        Assertions.assertEquals("JohnDoe", user.getName());
    }

    @Test
    void incorrectBirthdateUserTest() {
        UserController master = new UserController();
        User user = new User(0, "JD@post.com", "JohnDoe", "John", LocalDate.of(2980, 12, 12));
        UserValidationException thrown = assertThrows(
                UserValidationException.class,
                () -> master.createUser(user),
                "Дата рождения в будущем"
        );
    }
}

