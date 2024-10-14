package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;


@RestController
@RequestMapping("/users")
public class FriendsController {
    InMemoryUserStorage userStorage;

    @Autowired
    public FriendsController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable Long id, @PathVariable Long friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
            throw new UserValidationException("Пользователь отсутствует");
        }
        return id.toString()+friendId.toString();
    }
    //удаление из друзей
    //вывод списка общих друзей
    //проверка добавления друга
    private boolean checkFriendsAvalaibility(Long id, Long friendId) {
        return userStorage.findAllUsers().stream().anyMatch(user -> user.getId()==id) &&
                userStorage.findAllUsers().stream().anyMatch(user -> user.getId()==friendId);
    }

}

