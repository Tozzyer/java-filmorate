package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;

import java.time.LocalDate;
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
    @JsonIgnore
    private Set<Integer> whoLikes;

    public Film addLike(Integer id) {
        if(!(whoLikes ==null) && whoLikes.contains(id)){
            throw new FilmValidationException("Пользователь уже поставил лайк");
        }
        whoLikes.add(id);
        rating++;
        return this;
    }

    public Film removeLike(Integer id){
        if(whoLikes==null||!whoLikes.contains(id)){
            throw new FilmValidationException("Пользователь не ставил лайк");
        }
        whoLikes.remove(id);
        rating++;
        return this;
    }
}


