package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;


@RestController
@RequestMapping("/users")
public class FriendsController {
    InMemoryUserStorage userStorage;
    UserService userService;

    @Autowired
    public FriendsController(UserService userService) {
        this.userService = userService;
    }

    //добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        return userService.addFriend(id,friendId);
    }
    //удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        return userService.removeFriend(id,friendId);
    }
    //вывод списка общих друзей
    @GetMapping("/{id}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId)){

    }

}

