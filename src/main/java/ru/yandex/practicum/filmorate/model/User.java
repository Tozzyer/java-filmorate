package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
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
        log.info("Друзья пользователя " + id + ": " + friends);
        return this;
    }

    public User deleteFriend(Integer friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.remove(friendId);
        return this;
    }

    public Collection<Integer> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }

}
