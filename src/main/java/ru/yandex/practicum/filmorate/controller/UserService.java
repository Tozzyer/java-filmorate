package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Service
@RestController
@RequestMapping("/users")
public class UserService {
    InMemoryUserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable Long id, @PathVariable Long friendId){
        return id.toString()+friendId.toString();
    }
    //удаление из друзей
    //вывод списка общих друзей
    //проверка добавления друга
    private boolean checkFriend(Long id, Long friendId){
        return true;
    }
}
