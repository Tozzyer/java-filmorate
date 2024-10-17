package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Slf4j
public class UserService {

    InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //добавление в друзья
    public User addFriend(Integer id,Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
        throw new UserValidationException("Пользователь отсутствует");
        }
        User updatedFriend = userStorage.findAllUsers().stream()
                .filter(user -> user.getId()==friendId)
                .map(user ->user.addFriend(id))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        User updatedUser = userStorage.findAllUsers().stream()
                .filter(user -> user.getId()==id)
                .map(user ->user.addFriend(friendId))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        return userStorage.updateUser(updatedUser);
    }

    //удаление из друзей
    public User removeFriend(Integer id,Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
            throw new UserValidationException("Пользователь отсутствует");
        }
        User updatedFriend = userStorage.findAllUsers().stream()
                .filter(user -> user.getId()==friendId)
                .map(user ->user.deleteFriend(id))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        User updatedUser = userStorage.findAllUsers().stream()
                .filter(user -> user.getId()==id)
                .map(user ->user.deleteFriend(friendId))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        return userStorage.updateUser(updatedUser);
    }

    //вывод списка общих друзей
    public Collection<User> getCommonFriends(Integer id, Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
            throw new UserValidationException("Пользователь отсутствует");
        }
        User firstUser = userStorage.findAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
        User secondUser = userStorage.findAllUsers().stream()
                .filter(user -> user.getId() == friendId)
                .findFirst()
                .orElse(null);
        Set<Integer> commonId = firstUser.getFriends().stream()
                .filter(secondUser.getFriends()::contains)
                .collect(Collectors.toSet());
        return userStorage.findAllUsers().stream()
                .filter(user -> commonId.contains(user.getId()))
                .collect(Collectors.toList());
    }
    //проверка добавления друга
    private boolean checkFriendsAvalaibility(Integer id, Integer friendId) {
        return userStorage.findAllUsers().stream().anyMatch(user -> user.getId()==id) &&
                userStorage.findAllUsers().stream().anyMatch(user -> user.getId()==friendId);
    }

}
