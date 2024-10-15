package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

    public User addFriend(Integer friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friendId);
        return this;
    }

    public User deleteFriend(Integer friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.remove(friendId);
        return this;
    }
}
