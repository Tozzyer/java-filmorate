package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return inMemoryUserStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
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
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId){
        return userService.getCommonFriends(id,friendId);
    }

}