package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> findAllUsers();

    public User createUser(@RequestBody User user);

    public User updateUser(@RequestBody User user);

}
