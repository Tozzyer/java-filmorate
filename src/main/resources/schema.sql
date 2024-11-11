CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER PRIMARY KEY,
    genre_name VARCHAR
);

CREATE TABLE IF NOT EXISTS movie_genre (
    movie_id INTEGER,
    genre_id INTEGER,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
);

MERGE INTO genre (genre_id, genre_name) KEY(genre_id) VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER PRIMARY KEY,
    mpa_name VARCHAR
);

MERGE INTO mpa (mpa_id, mpa_name) KEY(mpa_id) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');


CREATE TABLE IF NOT EXISTS movie (
    movie_id INTEGER PRIMARY KEY,
    movie_name VARCHAR,
    movie_description VARCHAR,
    movie_release DATE,
    movie_duration TIME,
    genre_id INTEGER,
    mpa_id INTEGER
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY,
    user_name VARCHAR,
    user_email VARCHAR,
    user_birthdate DATE,
    user_login VARCHAR
);

CREATE TABLE IF NOT EXISTS user_friendlist (
    user_id INTEGER,
    friend_id INTEGER,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS movie_rating (
    movie_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY (movie_id, user_id)
);
