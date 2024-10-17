package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.inMemoryUserStorage = userStorage;
    }

    //добавление в друзья
    public User addFriend(Integer id,Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
        throw new UserValidationException("Пользователь отсутствует");
        }
        User updatedFriend = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId()==friendId)
                .map(user ->user.addFriend(id))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        User updatedUser = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId()==id)
                .map(user ->user.addFriend(friendId))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        return inMemoryUserStorage.updateUser(updatedUser);
    }

    //удаление из друзей
    public User removeFriend(Integer id,Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
            throw new UserValidationException("Пользователь отсутствует");
        }
        User updatedFriend = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId()==friendId)
                .map(user ->user.deleteFriend(id))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        User updatedUser = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId()==id)
                .map(user ->user.deleteFriend(friendId))
                .findFirst()
                .orElseThrow(()->new UserValidationException("Пользователь отсутствует"));

        return inMemoryUserStorage.updateUser(updatedUser);
    }

    //вывод списка общих друзей
    public Collection<User> getCommonFriends(Integer id, Integer friendId){
        if(!checkFriendsAvalaibility(id,friendId)){
            throw new UserValidationException("Пользователь отсутствует");
        }
        User firstUser = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
        User secondUser = inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> user.getId() == friendId)
                .findFirst()
                .orElse(null);
        Set<Integer> commonId = firstUser.getFriends().stream()
                .filter(secondUser.getFriends()::contains)
                .collect(Collectors.toSet());
        return inMemoryUserStorage.findAllUsers().stream()
                .filter(user -> commonId.contains(user.getId()))
                .collect(Collectors.toList());
    }
    //проверка добавления друга
    private boolean checkFriendsAvalaibility(Integer id, Integer friendId) {
        return inMemoryUserStorage.findAllUsers().stream().anyMatch(user -> user.getId()==id) &&
                inMemoryUserStorage.findAllUsers().stream().anyMatch(user -> user.getId()==friendId);
    }

}
