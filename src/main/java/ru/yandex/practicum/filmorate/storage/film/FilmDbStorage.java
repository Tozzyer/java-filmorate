package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.BadDataException;
import ru.yandex.practicum.filmorate.exceptions.UnknownDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
@Slf4j
@Component
@Primary

public class FilmDbStorage implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT movie_id, movie_name, movie_description, movie_release, movie_duration FROM movie";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("movie_id"));
            film.setName(rs.getString("movie_name"));
            film.setDescription(rs.getString("movie_description"));
            film.setReleaseDate(rs.getDate("movie_release").toLocalDate());
            String durationStr = rs.getString("movie_duration");
            String[] timeParts = durationStr.split(":");
            long durationInSeconds = Integer.parseInt(timeParts[0]) * 3600
                    + Integer.parseInt(timeParts[1]) * 60
                    + Integer.parseInt(timeParts[2]);
            film.setDuration(durationInSeconds);
            return film;
        });
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);

        long durationInSeconds = film.getDuration();
        String durationFormatted = String.format("%02d:%02d:%02d",
                (durationInSeconds / 3600),
                (durationInSeconds % 3600) / 60,
                durationInSeconds % 60);

        film.setId(getNextFilmId());

        String sql = "INSERT INTO movie (movie_id, movie_name, movie_description, movie_release, movie_duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                durationFormatted,
                film.getMpa() != null ? film.getMpa().getId() : null);
        //проверка на существование жанров
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "MERGE INTO movie_genre (movie_id, genre_id) KEY (movie_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        String checkSql = "SELECT COUNT(*) FROM movie WHERE movie_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, film.getId());
        if (count == null || count == 0) {
            throw new UnknownDataException("Фильм с таким ID не найден.");
        }
        String sql = "UPDATE movie SET movie_name = ?, movie_description = ?, movie_release = ?, movie_duration = ? WHERE movie_id = ?";
        long durationInSeconds = film.getDuration();
        String durationFormatted = String.format("%02d:%02d:%02d",
                (durationInSeconds / 3600),
                (durationInSeconds % 3600) / 60,
                durationInSeconds % 60);
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                durationFormatted,
                film.getId());
        return film;
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
        //Блок для проверки жанра и рейтинга
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() > 6) {
                    throw new BadDataException("Некорректный id жанра: " + genre.getId());
                }
            }
        }
        if (film.getMpa() != null && film.getMpa().getId() > 5) {
            throw new BadDataException("Некорректный id MPA: " + film.getMpa().getId());
        }
    }

    //Метод для получения нового уникального ID для пользователя
    private Integer getNextFilmId() {
        String sql = "SELECT COALESCE(MAX(movie_id), 0) FROM movie";
        Long currentMaxId = jdbcTemplate.queryForObject(sql, Long.class);
        return Math.toIntExact(currentMaxId + 1);
    }

    // Добавляем лайк
    public Film addLike(Integer filmId, Integer userId) {
        if (!((checkMovie(filmId)) && checkUser(userId))) {
            throw new UnknownDataException("Фильм или пользователь отсутствует");
        }
        String checkLikeQuery = "SELECT COUNT(*) FROM movie_rating WHERE movie_id = ? AND user_id = ?";
        int likeCount = jdbcTemplate.queryForObject(checkLikeQuery, Integer.class, filmId, userId);
        if (likeCount > 0) {
            throw new UnknownDataException("Пользователь уже поставил лайк этому фильму.");
        }
        String insertLikeQuery = "INSERT INTO movie_rating (movie_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(insertLikeQuery, filmId, userId);
        Film film = getFilmById(filmId);
        if (film != null) {
            if (film.getWhoLikes() == null) {
                film.setWhoLikes(new HashSet<>());
            }
            film.getWhoLikes().add(userId);
            film.setRating(film.getRating() + 1);
        }

        return film;
    }

    //Метод гета фильма по id из таблицы
    private Film getFilmById(Integer filmId) {
        String sql = "SELECT movie_id, movie_name, movie_description, movie_release, movie_duration " +
                "FROM movie WHERE movie_id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("movie_id"));
            film.setName(rs.getString("movie_name"));
            film.setDescription(rs.getString("movie_description"));
            film.setReleaseDate(rs.getDate("movie_release").toLocalDate());
            java.sql.Time time = rs.getTime("movie_duration");
            long durationInSeconds = time != null ? time.getTime() / 1000 : 0;
            film.setDuration(durationInSeconds);
            film.setWhoLikes(new HashSet<>());
            film.setRating(0);
            return film;
        }, filmId);
    }

    //Вывод рейтинга фильма
    public Collection<Film> top(Integer count) {
        log.info("Запрошен топ фильмов в количестве: " + count);
        String sql = "SELECT m.movie_id, m.movie_name, m.movie_description, m.movie_release, m.movie_duration, " +
                "COUNT(r.user_id) AS like_count " +
                "FROM movie m " +
                "LEFT JOIN movie_rating r ON m.movie_id = r.movie_id " +
                "GROUP BY m.movie_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("movie_id"));
            film.setName(rs.getString("movie_name"));
            film.setDescription(rs.getString("movie_description"));
            film.setReleaseDate(rs.getDate("movie_release").toLocalDate());
            film.setDuration(rs.getTime("movie_duration").toLocalTime().toSecondOfDay());
            film.setRating(rs.getInt("like_count"));
            film.setWhoLikes(new HashSet<>());

            return film;
        }, count);
        return films;
    }

    //Удаление лайка
    public Film removeLike(Integer filmId, Integer userId) {
        String checkLikeQuery = "SELECT COUNT(*) FROM movie_rating WHERE movie_id = ? AND user_id = ?";
        int likeCount = jdbcTemplate.queryForObject(checkLikeQuery, Integer.class, filmId, userId);

        if (likeCount == 0) {
            throw new UnknownDataException("Пользователь не ставил лайк этому фильму.");
        }
        String deleteLikeQuery = "DELETE FROM movie_rating WHERE movie_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteLikeQuery, filmId, userId);
        Film film = getFilmById(filmId);
        if (film != null) {
            if (film.getWhoLikes() == null) {
                film.setWhoLikes(new HashSet<>());
            }
            film.getWhoLikes().remove(userId);
            film.setRating(film.getRating() - 1);
        }
        return film;
    }

    public Boolean checkMovie(int filmId) {
        String sql = "SELECT COUNT(*) FROM movie WHERE movie_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count > 0;
    }

    public Boolean checkUser(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count > 0;
    }


    public List<Genre> getAllGenres() {
        log.info("Сработал метод запроса жанров");
        String sql = "SELECT genre_id, genre_name FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }

    public Genre getGenre(Integer id) {
        if (id > 6) {
            throw new UnknownDataException("Запрашиваемый жанр отсутствует");
        }
        String sql = "SELECT genre_id, genre_name FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);
    }

    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id, mpa_name FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }

    public Mpa getMpa(Integer id) {
        if (id > 5) {
            throw new UnknownDataException("Запрашиваемый жанр отсутствует");
        }
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
    }

    //Гетаем фильм
    public Film getFilm(Integer id) {
        String sql = "SELECT movie_id, movie_name, movie_description, movie_release, movie_duration, mpa_id " +
                "FROM movie WHERE movie_id = ?";

        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Film result = new Film();
            result.setId(rs.getInt("movie_id"));
            result.setName(rs.getString("movie_name"));
            result.setDescription(rs.getString("movie_description"));
            result.setReleaseDate(rs.getDate("movie_release").toLocalDate());
            result.setDuration(rs.getTime("movie_duration").toLocalTime().toSecondOfDay());

            int mpaId = rs.getInt("mpa_id");
            if (mpaId != 0) {
                result.setMpa(getMpa(mpaId));
            }

            return result;
        }, id);

        if (film == null) {
            throw new UnknownDataException("Фильм с ID " + id + " не найден.");
        }
        film.setGenres(getGenresByFilmId(id));
        return film;
    }

    //Вытаскиваем жанры по id фильма
    private Collection<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM movie_genre mg " +
                "JOIN genre g ON mg.genre_id = g.genre_id " +
                "WHERE mg.movie_id = ?";
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId));
        return genres.isEmpty() ? Collections.emptyList() : genres;
    }

}
