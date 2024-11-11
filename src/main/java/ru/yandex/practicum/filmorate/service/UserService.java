package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;


@Service
@Slf4j
public class UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userDbStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        return userDbStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userDbStorage.updateUser(user);
    }


    //добавление в друзья
    public User addFriend(Integer id, Integer friendId) {
        return userDbStorage.addFriend(id, friendId);
    }

    //удаление из друзей
    public User removeFriend(Integer id, Integer friendId) {
        return userDbStorage.removeFriend(id, friendId);
    }

    //вывод списка общих друзей
    public Collection<User> getCommonFriends(Integer id, Integer friendId) {
        return userDbStorage.getCommonFriends(id, friendId);
    }

    //кто сказал что нет друзей
    public Collection<User> getFriends(Integer id) {
        return userDbStorage.getFriends(id);
    }

}
