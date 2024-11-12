package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<User> friends;

    public Collection<User> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }

}
