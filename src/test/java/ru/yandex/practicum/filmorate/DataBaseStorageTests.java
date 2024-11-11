package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({UserDbStorage.class, FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DataBaseStorageTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;


    @Test
    void testCreateAndFindUser() {
        User newUser = new User();
        newUser.setEmail("test@qwerty.com");
        newUser.setLogin("testuser");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(newUser);
        Optional<User> retrievedUser = Optional.ofNullable(userStorage.getUserById(createdUser.getId()));

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@qwerty.com");
    }

    @Test
    void testUpdateUser() {
        User newUser = new User();
        newUser.setEmail("test@drhdhr.com");
        newUser.setLogin("test");
        newUser.setName("Test Test");
        newUser.setBirthday(LocalDate.of(1995, 5, 15));

        User createdUser = userStorage.createUser(newUser);
        createdUser.setName("New Test");
        User updatedUser = userStorage.updateUser(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("New Test");
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@dhrhd.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@hhdhdr.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Collection<User> users = userStorage.findAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@tsw.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@afawf.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        User user3 = new User();
        user3.setEmail("user3@blabla.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1993, 3, 3));

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);
        User createdUser3 = userStorage.createUser(user3);

        userStorage.addFriend(createdUser1.getId(), createdUser3.getId());
        userStorage.addFriend(createdUser2.getId(), createdUser3.getId());

        Collection<User> commonFriends = userStorage.getCommonFriends(createdUser1.getId(), createdUser2.getId());
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(createdUser3));
    }

    @Test
    void testCreateAndGetFilm() {
        Film newFilm = new Film();
        newFilm.setName("H8full 8");
        newFilm.setDescription("Western Detective");
        newFilm.setReleaseDate(LocalDate.of(2016, 1, 1));
        newFilm.setDuration(167);

        Film createdFilm = filmStorage.createFilm(newFilm);
        Optional<Film> retrievedFilm = Optional.ofNullable(filmStorage.getFilm(createdFilm.getId()));

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("H8full 8");
    }

    @Test
    void testUpdateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Avengers: Infinity War");
        newFilm.setDescription("Popcorn");
        newFilm.setReleaseDate(LocalDate.of(2021, 1, 1));
        newFilm.setDuration(150);

        Film createdFilm = filmStorage.createFilm(newFilm);
        createdFilm.setName("Avengers: Final");
        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Avengers: Final");
    }

    @Test
    void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("Movie47");
        film1.setDescription("doubtful");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(100);

        Film film2 = new Film();
        film2.setName("Movie48");
        film2.setDescription("better");
        film2.setReleaseDate(LocalDate.of(2019, 2, 2));
        film2.setDuration(110);

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);

        Collection<Film> films = filmStorage.findAllFilms();
        assertEquals(2, films.size());
    }

    @Test
    void testGetTopFilms() {
        Film film1 = new Film();
        film1.setName("XXX");
        film1.setDescription("XXX");
        film1.setReleaseDate(LocalDate.of(2021, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Sinister");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(90);

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);

        User newUser = new User();
        newUser.setEmail("test@qwerty.com");
        newUser.setLogin("testuser");
        newUser.setName("Test User1");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.createUser(newUser);
        User newUser2 = new User();
        newUser.setEmail("test@fawf.com");
        newUser.setLogin("testuser");
        newUser.setName("Test User2");
        newUser.setBirthday(LocalDate.of(1992, 5, 6));
        User createdUser2 = userStorage.createUser(newUser);

        filmStorage.addLike(film2.getId(), 1);
        filmStorage.addLike(film2.getId(), 2);

        Collection<Film> topFilms = filmStorage.top(1);
        assertEquals(1, topFilms.size());
        assertThat(topFilms.iterator().next().getName()).isEqualTo("Sinister");
    }
}
