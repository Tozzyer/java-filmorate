package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.UnknownDataException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private int rating = 0;
    private Genre genre;
    private Mpa mpa;
    @JsonIgnore
    private Set<Integer> whoLikes;

    public Film addLike(Integer id) {
        if (whoLikes == null) {
            whoLikes = new HashSet<>();
        }
        if (whoLikes.contains(id)) {
            throw new UnknownDataException("Пользователь уже поставил лайк");
        }
        whoLikes.add(id);
        rating++;
        return this;
    }

    public Film removeLike(Integer id) {
        if (whoLikes == null) {
            whoLikes = new HashSet<>();
        }
        if (!whoLikes.contains(id)) {
            throw new UnknownDataException("Пользователь не ставил лайк");
        }
        whoLikes.remove(id);
        rating++;
        return this;
    }

    public enum Genre {
        Comedy,
        Drama,
        Animation,
        Thriller,
        Documentary,
        Action
    }

    public enum Mpa {
        G,
        PG,
        PG13,
        R,
        NC17
    }
}



